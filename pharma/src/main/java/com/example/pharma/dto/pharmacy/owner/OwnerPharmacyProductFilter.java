package com.example.pharma.dto.pharmacy.owner;

import com.example.pharma.model.entity.inventory.AvailabilityStatus;

import java.math.BigDecimal;

public record OwnerPharmacyProductFilter(
        String productName,
        AvailabilityStatus availabilityStatus,
        String categoryName,
        BigDecimal minPrice,
        BigDecimal maxPrice

) {
}
