package com.example.pharma.repository.Pharmacy;

import com.example.pharma.model.entity.pharmacy.Pharmacy;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalTime;

public class PharmacySpecifications {
    private PharmacySpecifications()
    {

    }
    public static Specification<Pharmacy> hasName(String name) {
        return (root, query, cb) ->
                name == null ? cb.conjunction() :
                        cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<Pharmacy> hasMinRating(Float rating) {
        return (root, query, cb) ->
                rating == null ? cb.conjunction() :
                        cb.greaterThanOrEqualTo(root.get("totalRating"), rating);
    }

    public static Specification<Pharmacy> isOpenNow(Boolean isOpen) {
        if (!Boolean.TRUE.equals(isOpen)) {
            return (root, query, cb) -> cb.conjunction();
        }
        // if the user requested open pharmacies, apply the open-now logic
        return (root, query, cb) -> {

            LocalTime now = LocalTime.now();

            Expression<Boolean> is24Hours = root.get("is24Hours");
            Expression<LocalTime> opening = root.get("openingTime");
            Expression<LocalTime> closing = root.get("closingTime");

            // 24h pharmacies
            Predicate alwaysOpen = cb.isTrue(is24Hours);

            // Normal same-day hours (opening <= closing)
            Predicate normalHours = cb.and(
                    cb.lessThanOrEqualTo(opening, closing),
                    cb.lessThanOrEqualTo(opening, now),
                    cb.greaterThanOrEqualTo(closing, now)
            );

            // Overnight hours (opening > closing)
            Predicate overnightHours = cb.and(
                    cb.greaterThan(opening, closing),
                    cb.or(
                            cb.lessThanOrEqualTo(opening, now),   // time >= opening
                            cb.greaterThanOrEqualTo(closing, now) // time <= closing
                    )
            );

            return cb.or(alwaysOpen, normalHours, overnightHours);
        };
    }
}
