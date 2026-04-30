package com.example.pharma.dto.P2P;

import com.example.pharma.model.entity.P2P.ReservationStatus;

import java.time.LocalDateTime;

public record ReservationResponse(
        Long reservationId,
        Long userId,
        Long productId,
        String productName,
        ReservationStatus status,
        LocalDateTime createdAt
) {}

