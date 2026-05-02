package com.example.pharma.specification;


import com.example.pharma.dto.pharmacy.owner.OwnerPharmacyProductFilter;
import com.example.pharma.model.entity.inventory.AvailabilityStatus;
import com.example.pharma.model.entity.inventory.PharmacyProduct;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class OwnerPharmacyProductSpecification {

    private OwnerPharmacyProductSpecification() {}

    public static Specification<PharmacyProduct> build(
            Long pharmacyId,
            OwnerPharmacyProductFilter filter
    ) {
        return Specification
                .where(belongsToPharmacy(pharmacyId))
                .and(productNameContains(filter.productName()))
                .and(hasAvailabilityStatus(filter.availabilityStatus()))
                .and(priceGreaterThanOrEqual(filter.minPrice()))
                .and(priceLessThanOrEqual(filter.maxPrice()));
    }

    private static Specification<PharmacyProduct> belongsToPharmacy(Long pharmacyId) {
        return (root, query, cb) ->
                cb.equal(root.get("pharmacy").get("pharmacyId"), pharmacyId);
    }

    private static Specification<PharmacyProduct> productNameContains(String productName) {
        return (root, query, cb) -> {
            if (productName == null || productName.isBlank()) {
                return cb.conjunction();
            }

            String searchValue = "%" + productName.trim().toLowerCase() + "%";

            return cb.like(
                    cb.lower(root.get("product").get("name")),
                    searchValue
            );
        };
    }

    private static Specification<PharmacyProduct> hasAvailabilityStatus(AvailabilityStatus status) {
        return (root, query, cb) ->
                status == null
                        ? cb.conjunction()
                        : cb.equal(root.get("availabilityStatus"), status);
    }

    private static Specification<PharmacyProduct> priceGreaterThanOrEqual(BigDecimal minPrice) {
        return (root, query, cb) ->
                minPrice == null
                        ? cb.conjunction()
                        : cb.greaterThanOrEqualTo(root.get("price"), minPrice);
    }

    private static Specification<PharmacyProduct> priceLessThanOrEqual(BigDecimal maxPrice) {
        return (root, query, cb) ->
                maxPrice == null
                        ? cb.conjunction()
                        : cb.lessThanOrEqualTo(root.get("price"), maxPrice);
    }
}