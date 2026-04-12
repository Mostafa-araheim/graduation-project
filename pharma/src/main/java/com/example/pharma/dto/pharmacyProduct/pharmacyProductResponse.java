package com.example.pharma.dto.pharmacyProduct;

public record pharmacyProductResponse(
        Long id,
        Long pharmacyId,
        Long productId,
        String productName,
        String productImage,
        Double price,
        Double originalPrice,
        Boolean inStock,
        String category,
        String pharmacyName,
        Double pharmacyLatitude,
        Double pharmacyLongitude,
        Double pharmacyDistance
) {}
