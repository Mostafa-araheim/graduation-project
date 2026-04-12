package com.example.pharma.specification;
import com.example.pharma.dto.Product.ProductFilter;
import com.example.pharma.model.entity.catalog.Product;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class ProductSpecification {

    private ProductSpecification() {
    }

    public static Specification<Product> buildFromFilter(ProductFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
                var categoryFetch = root.fetch("category", JoinType.INNER);
                root.fetch("brand", JoinType.LEFT);
            var categoryJoin = (Join<Object, Object>) categoryFetch;

            if (filter.categoryName() != null && !filter.categoryName().isBlank()) {
                predicates.add(buildCategoryFilter(categoryJoin, cb, filter));
            }

            if (filter.productName() != null && !filter.productName().isBlank()) {
                predicates.add(buildProductNameFilter(root, cb, filter));
            }

            if (filter.dosageForm() != null) {
                predicates.add(buildDosageFormFilter(root, cb, filter));
            }

//            if (filter.minPrice() != null) {
//                predicates.add(buildMinPriceFilter(pharmacyProductJoin, cb, filter));
//            }
//
//            if (filter.maxPrice() != null) {
//                predicates.add(buildMaxPriceFilter(pharmacyProductJoin, cb, filter));
//            }
//
//            if (filter.inStock() != null) {
//                predicates.add(buildInStockFilter(pharmacyProductJoin, cb, filter));
//            }

//            if (filter.maxDistanceKm() != null && filter.maxDistanceKm() > 0
//                    && filter.userLatitude() != null && filter.userLongitude() != null) {
//                Geometry polygon = locationService.getRoadReachPolygon(
//                        filter.userLatitude(),
//                        filter.userLongitude(),
//                        filter.maxDistanceKm() * 1000.0
//                );
//                var distancePredicate = buildDistanceFilter(pharmacyJoin, cb, polygon);
//                predicates.add(distancePredicate);
//            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

//    public static Specification<Product> byId(Long id) {
//        return buildBaseJoins()
//                .and((root, query, cb) -> cb.equal(root.get("productId"), id));
//    }
    private static Predicate buildCategoryFilter(Join<?, ?> categoryJoin,
                                                 CriteriaBuilder cb,
                                                 ProductFilter filter) {
        return cb.equal(
                cb.lower(categoryJoin.get("categoryName")),
                filter.categoryName().toLowerCase()
        );
    }

    private static Predicate buildProductNameFilter(Root<Product> root,
                                                    CriteriaBuilder cb,
                                                    ProductFilter filter) {
        return cb.like(
                cb.lower(root.get("name")),
                "%" + filter.productName().toLowerCase() + "%"
        );
    }

    private static Predicate buildDosageFormFilter(Root<Product> root,
                                                   CriteriaBuilder cb,
                                                   ProductFilter filter) {
        return cb.equal(root.get("dosageForm"), filter.dosageForm());
    }

//    private static Predicate buildMinPriceFilter(Join<?, ?> pharmacyProductJoin,
//                                                 CriteriaBuilder cb,
//                                                 ProductFilter filter) {
//        return cb.greaterThanOrEqualTo(pharmacyProductJoin.get("price"), filter.minPrice());
//    }
//
//    private static Predicate buildMaxPriceFilter(Join<?, ?> pharmacyProductJoin,
//                                                 CriteriaBuilder cb,
//                                                 ProductFilter filter) {
//        return cb.lessThanOrEqualTo(pharmacyProductJoin.get("price"), filter.maxPrice());
//    }
//
//    private static Predicate buildInStockFilter(Join<?, ?> pharmacyProductJoin,
//                                                CriteriaBuilder cb,
//                                                ProductFilter filter) {
//        return cb.equal(
//                pharmacyProductJoin.get("availabilityStatus"),
//                Boolean.TRUE.equals(filter.inStock()) ? AvailabilityStatus.Available : AvailabilityStatus.OutOfStock
//        );
//    }
//
//    private static Predicate buildDistanceFilter(Join<?, ?> lastJoin,
//                                                 CriteriaBuilder cb,
//                                                 Geometry isochronePolygon) {
//        // Convert JTS Geometry to WKT so Hibernate/JDBC can bind it as a string
//        String wkt = new WKTWriter().write(isochronePolygon);
//
//        // ST_GeomFromText(wkt, srid) tells PostGIS how to interpret the literal
//        var polygonExpr = cb.function(
//                "ST_GeomFromText", Geometry.class,
//                cb.literal(wkt),
//                cb.literal(4326)
//        );
//
//        return cb.isTrue(
//                cb.function("ST_Within", Boolean.class,
//                        lastJoin.get("location"),
//                        polygonExpr
//                )
//        );
//    }
//    private static Specification<Product> buildBaseJoins() {
//        return (root, query, cb) -> {
//            if (query != null && query.getResultType() != Long.class && query.getResultType() != long.class) {
//                query.distinct(true);
//                root.fetch("category", JoinType.INNER);
//                root.fetch("brand", JoinType.LEFT);
//            }
//            var pharmacyProductJoin = root.join("pharmacyProducts", JoinType.INNER);
//            var inventoryJoin = pharmacyProductJoin.join("inventory", JoinType.INNER);
//            inventoryJoin.join("pharmacy", JoinType.INNER);
//            root.join("category", JoinType.INNER);
//            return cb.conjunction();
//        };
//    }
}
