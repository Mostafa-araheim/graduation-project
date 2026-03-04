package com.example.pharma.specification;

import com.example.pharma.dto.Medicine.MedicineFilter;
import com.example.pharma.model.entity.catalog.Medicine;
import com.example.pharma.model.entity.inventory.AvailabilityStatus;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class MedicineSpecification {

    private MedicineSpecification() {
    }

    public static Specification<Medicine> buildFromFilter(MedicineFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            query.distinct(true);

            if (filter.categoryName() != null && !filter.categoryName().isBlank()) {
                predicates.add(buildCategoryFilter(root, cb, filter));
            }

            if (filter.minPrice() != null) {
                predicates.add(buildMinPriceFilter(root, cb, filter));
            }

            if (filter.maxPrice() != null) {
                predicates.add(buildMaxPriceFilter(root, cb, filter));
            }

            if (Boolean.TRUE.equals(filter.inStock())) {
                predicates.add(buildInStockFilter(root, cb));
            }

            if (filter.maxDistanceKm() != null && filter.maxDistanceKm() > 0
                    && filter.userLatitude() != null && filter.userLongitude() != null) {
                var distancePredicate = buildDistanceFilter(root, cb, query, filter);
                predicates.add(distancePredicate);
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static Predicate buildCategoryFilter(Root<Medicine> root,
                                                 CriteriaBuilder cb,
                                                 MedicineFilter filter) {
        var categoryJoin = root.join("category", JoinType.INNER);
        return cb.equal(
                cb.lower(categoryJoin.get("categoryName")),
                filter.categoryName().toLowerCase()
        );
    }

    private static Predicate buildMinPriceFilter(Root<Medicine> root,
                                                 CriteriaBuilder cb,
                                                 MedicineFilter filter) {
        return cb.greaterThanOrEqualTo(root.get("price"), filter.minPrice());
    }

    private static Predicate buildMaxPriceFilter(Root<Medicine> root,
                                                 CriteriaBuilder cb,
                                                 MedicineFilter filter) {
        return cb.lessThanOrEqualTo(root.get("price"), filter.maxPrice());
    }

    private static Predicate buildInStockFilter(Root<Medicine> root,
                                                CriteriaBuilder cb) {
        var inventoryRecordJoin = root.join("inventoryRecords", JoinType.INNER);
        return cb.equal(
                inventoryRecordJoin.get("availabilityStatus"),
                AvailabilityStatus.Available
        );
    }

    private static Predicate buildDistanceFilter(Root<Medicine> root,
                                                 CriteriaBuilder cb,
                                                 CriteriaQuery<?> query,
                                                 MedicineFilter filter) {
        var inventoryRecordJoin = root.join("inventoryRecords", JoinType.INNER);
        var inventoryJoin = inventoryRecordJoin.join("inventory", JoinType.INNER);
        var pharmacyJoin = inventoryJoin.join("pharmacy", JoinType.INNER);
        Expression<Double> distanceInMeters = cb.function(
                "ST_DistanceSphere",
                Double.class,
                cb.function("ST_MakePoint", Object.class,
                        cb.literal(filter.userLongitude()).as(Double.class),
                        cb.literal(filter.userLatitude()).as(Double.class)
                ),
                cb.function("ST_MakePoint", Object.class,
                        pharmacyJoin.get("longitude").as(Double.class),
                        pharmacyJoin.get("latitude").as(Double.class)
                )
        );

        Predicate distancePredicate = cb.lessThanOrEqualTo(
                distanceInMeters,
                filter.maxDistanceKm() * 1000.0
        );
//        query.orderBy(cb.asc(distanceInMeters));
        return distancePredicate;

    }
}
