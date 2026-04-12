package com.example.pharma.specification;

import com.example.pharma.dto.pharmacyProduct.PharmacyProductFilter;
import com.example.pharma.model.entity.inventory.AvailabilityStatus;
import com.example.pharma.model.entity.inventory.PharmacyProduct;
import com.example.pharma.service.LocationService;
import jakarta.persistence.criteria.*;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.WKTWriter;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class PharmacyProductSpecification {

    private PharmacyProductSpecification() {
    }

    public static Specification<PharmacyProduct> buildFromFilter(PharmacyProductFilter filter, LocationService locationService) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            var productFetch = root.fetch("product", JoinType.INNER);
            var productJoin = (Join<Object, Object>) productFetch;

            var categoryFetch = productFetch.fetch("category", JoinType.INNER);
            var categoryJoin = (Join<Object, Object>) categoryFetch;

            var brandFetch = productFetch.fetch("brand", JoinType.LEFT);
            var brandJoin = (Join<Object, Object>) brandFetch;

            var inventoryFetch = root.fetch("inventory", JoinType.INNER);
            var inventoryJoin = (Join<Object, Object>) inventoryFetch;

            var pharmacyFetch = inventoryFetch.fetch("pharmacy", JoinType.INNER);
            pharmacyFetch.fetch("address", JoinType.LEFT);
            var pharmacyJoin = (Join<Object, Object>) pharmacyFetch;

            if (filter.categoryName() != null && !filter.categoryName().isBlank()) {
                predicates.add(buildCategoryFilter(categoryJoin, cb, filter));
            }

            if (filter.productName() != null && !filter.productName().isBlank()) {
                predicates.add(buildProductNameFilter(productJoin, cb, filter));
            }

            if (filter.dosageForm() != null) {
                predicates.add(buildDosageFormFilter(productJoin, cb, filter));
            }

            if (filter.minPrice() != null) {
                predicates.add(buildMinPriceFilter(root, cb, filter));
            }

            if (filter.maxPrice() != null) {
                predicates.add(buildMaxPriceFilter(root, cb, filter));
            }

            if (filter.inStock() != null) {
                predicates.add(buildInStockFilter(root, cb, filter));
            }

            if (filter.maxDistanceKm() != null && filter.maxDistanceKm() > 0
                    && filter.userLatitude() != null && filter.userLongitude() != null) {
                Geometry polygon = locationService.getRoadReachPolygon(
                        filter.userLatitude(),
                        filter.userLongitude(),
                        filter.maxDistanceKm() * 1000.0
                );
                var distancePredicate = buildDistanceFilter(pharmacyJoin, cb, polygon);
                predicates.add(distancePredicate);
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static Predicate buildCategoryFilter(Join<?, ?> categoryJoin,
                                                 CriteriaBuilder cb,
                                                 PharmacyProductFilter filter) {
        return cb.equal(
                cb.lower(categoryJoin.get("categoryName")),
                filter.categoryName().toLowerCase()
        );
    }

    private static Predicate buildProductNameFilter(Join<?, ?> productJoin,
                                                    CriteriaBuilder cb,
                                                    PharmacyProductFilter filter) {
        return cb.like(
                cb.lower(productJoin.get("name")),
                "%" + filter.productName().toLowerCase() + "%"
        );
    }

    private static Predicate buildDosageFormFilter(Join<?, ?> productJoin,
                                                   CriteriaBuilder cb,
                                                   PharmacyProductFilter filter) {
        return cb.equal(productJoin.get("dosageForm"), filter.dosageForm());
    }

    private static Predicate buildMinPriceFilter(Root<PharmacyProduct> root,
                                                 CriteriaBuilder cb,
                                                 PharmacyProductFilter filter) {
        return cb.greaterThanOrEqualTo(root.get("price"), filter.minPrice());
    }

    private static Predicate buildMaxPriceFilter(Root<PharmacyProduct> root,
                                                 CriteriaBuilder cb,
                                                 PharmacyProductFilter filter) {
        return cb.lessThanOrEqualTo(root.get("price"), filter.maxPrice());
    }

    private static Predicate buildInStockFilter(Root<PharmacyProduct> root,
                                                CriteriaBuilder cb,
                                                PharmacyProductFilter filter) {
        return cb.equal(
                root.get("availabilityStatus"),
                Boolean.TRUE.equals(filter.inStock()) ? AvailabilityStatus.Available : AvailabilityStatus.OutOfStock
        );
    }

    private static Predicate buildDistanceFilter(Join<?, ?> pharmacyJoin,
                                                 CriteriaBuilder cb,
                                                 Geometry isochronePolygon) {
        String wkt = new WKTWriter().write(isochronePolygon);
        var polygonExpr = cb.function(
                "ST_GeomFromText", Geometry.class,
                cb.literal(wkt),
                cb.literal(4326)
        );

        return cb.isTrue(
                cb.function("ST_Within", Boolean.class,
                        pharmacyJoin.get("location"),
                        polygonExpr
                )
        );
    }
}
