package com.example.pharma.dto.cart.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CartItemRequest(

        @NotNull(message = "pharmacyProductId is required")
        @Positive(message = "pharmacyProductId must be positive")
        Long pharmacyProductId,

        @NotNull(message = "Quantity is required")
        @Positive(message = "Quantity must be greater than 0")
        Long quantity
) {}