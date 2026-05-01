package com.example.pharma.dto.pharmacyProduct;

import com.fasterxml.jackson.annotation.JsonProperty;

public record pharmacyProductResponse(
        @JsonProperty("pharmacy_product_id") Long id,
        @JsonProperty("pharmacy_id")Long pharmacyId,
        @JsonProperty("product_id") Long productId,
        @JsonProperty("product_name") String productName,
        @JsonProperty("product_image") String productImage,
        @JsonProperty("price") Double price,
        @JsonProperty("original_price") Double originalPrice,
        @JsonProperty("in_stock") Boolean inStock,
        @JsonProperty("category") String category,
        @JsonProperty("pharmacy_name") String pharmacyName,
        @JsonProperty("pharmacy_latitude") Double pharmacyLatitude,
        @JsonProperty("pharmacy_longitude") Double pharmacyLongitude,
        @JsonProperty("pharmacy_distance") Double pharmacyDistance
) {}
