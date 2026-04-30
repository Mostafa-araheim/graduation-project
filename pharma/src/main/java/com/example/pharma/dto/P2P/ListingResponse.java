package com.example.pharma.dto.P2P;

import com.example.pharma.model.entity.P2P.ListingStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ListingResponse(
        Long listingId,
        Long productId,
        String productName,
        Long sellerId,
        String sellerName,
        Long quantity,
        Float price,
        LocalDate expiryDate,
        String additionalDetails,
        String imageUrl,
        ListingStatus status,
        LocalDateTime createdAt
) {}

