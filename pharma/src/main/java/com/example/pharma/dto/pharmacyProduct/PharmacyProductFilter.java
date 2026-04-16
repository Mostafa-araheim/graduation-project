package com.example.pharma.dto.pharmacyProduct;

import com.example.pharma.model.entity.catalog.DosageForm;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.DecimalMin;

public record PharmacyProductFilter(
    Long productId,
    String productName,
    String categoryName,
    @Enumerated(EnumType.STRING)
    DosageForm dosageForm,
    Double userLatitude,
    Double userLongitude,

    @DecimalMin(value = "1.0", message = "Distance cannot be negative or zero")
    Double maxDistanceKm,

    @DecimalMin(value = "0.0", message = "Minimum price cannot be negative")
    Double minPrice,

    @DecimalMin(value = "0.0", message = "Maximum price cannot be negative")
    Double maxPrice,
    Boolean inStock
) {}
