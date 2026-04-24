package com.example.pharma.dto.pharmacyProduct;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record UpdatePharmacyProductRequest(

        @PositiveOrZero(message = "Quantity cannot be negative")
        @JsonProperty("quantity")
        Long quantity,

        @DecimalMin(value = "0.01", message = "Price must be positive")
        @JsonProperty("price")
        BigDecimal price
) {
}