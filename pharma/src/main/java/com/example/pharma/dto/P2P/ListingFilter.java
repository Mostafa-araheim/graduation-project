package com.example.pharma.dto.P2P;

public record ListingFilter(
        String city,
        String condition,
        String categoryName,
        String search
) {}