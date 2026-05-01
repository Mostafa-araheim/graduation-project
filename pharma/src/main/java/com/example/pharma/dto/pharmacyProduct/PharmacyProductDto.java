package com.example.pharma.dto.pharmacyProduct;

import com.example.pharma.model.entity.catalog.DosageForm;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public record PharmacyProductDto(
        @JsonProperty("pharmacy_product_id") Long pharmacyProductId,
        @JsonProperty("pharmacy_name") String pharmacyName,

        @JsonProperty("product_id") Long productId,
        @JsonProperty("product_name") String productName,
        @JsonProperty("description") String description,
        @JsonProperty("product_image") String productImage,

        @JsonProperty("price") BigDecimal price,
        @JsonProperty("quantity") Integer quantity,
        @JsonProperty("in_stock") Boolean inStock,

        @JsonProperty("requires_prescription") boolean requiresPrescription,
        @JsonProperty("dosage_form") DosageForm dosageForm,
        @JsonProperty("strength") String strength,
        @JsonProperty("manufacturer") String manufacturer,

        @JsonProperty("category_id") Long categoryId,
        @JsonProperty("category_name") String categoryName,

        @JsonProperty("brand_id") Long brandId,
        @JsonProperty("brand_name") String brandName) {
}
