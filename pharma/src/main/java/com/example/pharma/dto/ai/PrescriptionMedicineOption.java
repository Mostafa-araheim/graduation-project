package com.example.pharma.dto.ai;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public record PrescriptionMedicineOption(
        @JsonProperty("pharmacy_product_id") Long pharmacyProductId,
        @JsonProperty("product_id")          Long productId,
        @JsonProperty("product_name")        String productName,
        @JsonProperty("product_image")       String productImage,
        @JsonProperty("dosage_form")         String dosageForm,
        @JsonProperty("strength")            String strength,
        @JsonProperty("price")               BigDecimal price,
        @JsonProperty("quantity_available")  Long quantityAvailable
) {}
