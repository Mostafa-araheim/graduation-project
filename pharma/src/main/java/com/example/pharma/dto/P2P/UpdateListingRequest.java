package com.example.pharma.dto.P2P;

import jakarta.validation.constraints.Min;

public record UpdateListingRequest(
        @Min(value = 1, message = "Quantity must be at least 1")
        Long quantity,

        String additionalDetails,

        @Min(value = 0, message = "Price cannot be negative")
        Float price,

        String imageUrl
) {}
