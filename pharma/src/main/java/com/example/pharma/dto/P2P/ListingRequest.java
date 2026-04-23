package com.example.pharma.dto.P2P;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record ListingRequest(
        @NotNull(message = "Product ID is required")
        Long productId,

        @NotNull(message = "Seller ID is required")
        Long sellerId,

        @NotNull(message = "Quantity is required")
        @Min(value = 1, message = "Quantity must be at least 1")
        Long quantity,

        String Category,
        String additionalDetails,

        @NotNull(message = "Price is required")
        @Min(value = 0, message = "Price cannot be negative")
        Float price,

        @NotNull(message = "Expiry date is required")
        @Future(message = "Expiry date must be in the future")
        LocalDate expiryDate,

        String imageUrl
) {}
