package com.example.pharma.dto.cart.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record UpdateCartItemQuantityRequest(

        @NotNull(message = "InventoryId is required")
        @Positive(message = "InventoryId must be positive")
        Long inventoryId,

        @NotNull(message = "MedicineId is required")
        @Positive(message = "MedicineId must be positive")
        Long medicineId,

        @NotNull(message = "Quantity is required")
        @Positive(message = "Quantity must be greater than 0")
        Long quantity
) {}