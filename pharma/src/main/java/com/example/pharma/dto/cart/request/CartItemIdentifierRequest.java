package com.example.pharma.dto.cart.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CartItemIdentifierRequest(

        @NotNull(message = "InventoryId is required")
        @Positive(message = "InventoryId must be positive")
        Long pharmacyProductId

) {}
