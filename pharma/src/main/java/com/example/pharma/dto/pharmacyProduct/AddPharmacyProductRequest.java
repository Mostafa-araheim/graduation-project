package com.example.pharma.dto.pharmacyProduct;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record AddPharmacyProductRequest(

        @NotNull
        @JsonProperty("product_id")
        Long productId,

        @NotNull
        @Positive
        @JsonProperty("quantity")
        Long quantity,

        @NotNull
        @DecimalMin(value = "0.01", message = "Price must be positive")
        @JsonProperty("price")
        BigDecimal price
) {
}