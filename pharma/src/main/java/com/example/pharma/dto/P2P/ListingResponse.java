package com.example.pharma.dto.P2P;

import com.example.pharma.model.entity.P2P.ListingStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ListingResponse(
        Long listingId, // Present
        Long productId, //present
        String productName, //present
        Long sellerId, //Not required
        String sellerName,//Present
        String sellerPhoneNumber,//Present
        String categoryName,//Added
        String condition, //Added
        Long quantity,//Present
        Float price,//Present
        LocalDate expiryDate, //present
        String description,//present
        String imageUrl,//Present
        ListingStatus status,
        LocalDateTime createdAt,//Present
        String city
) {}

//interface Listing {
//
//
//
//    location: string;
//    verified?: boolean;
//}