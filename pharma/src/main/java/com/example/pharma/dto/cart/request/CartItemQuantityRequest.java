package com.example.pharma.dto.cart.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CartItemQuantityRequest(

        @NotNull(message = "InventoryRecordId is required")
        @Positive(message = "InventoryRecordId must be positive")
        Long pharmacyProductId,

        @NotNull(message = "Quantity is required")
        @Positive(message = "Quantity must be greater than 0")
        Long quantity
) {}