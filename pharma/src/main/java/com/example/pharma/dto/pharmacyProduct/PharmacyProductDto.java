package com.example.pharma.dto.pharmacyProduct;

import com.example.pharma.model.entity.catalog.DosageForm;
import com.example.pharma.model.entity.inventory.AvailabilityStatus;

import java.math.BigDecimal;

public record PharmacyProductDto(
                                 Long pharmacyProductId,
                                 String pharmacyName,
                                 BigDecimal price,
                                 Integer quantity,
                                 AvailabilityStatus availabilityStatus,
                                 Long productId,
                                 String productName,
                                 String description,
                                 boolean requiresPrescription,
                                 DosageForm dosageForm,
                                 String strength,
                                 String manufacturer,
                                 Long categoryId,
                                 String categoryName,
                                 Long brandId,
                                 String brandName) {
}
