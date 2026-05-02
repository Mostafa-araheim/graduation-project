package com.example.pharma.dto.P2P;

import jakarta.validation.constraints.NotNull;

public record ReservationRequest(
        @NotNull(message = "Product ID is required")
        Long productId
) {}

