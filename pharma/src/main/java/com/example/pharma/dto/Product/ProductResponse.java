package com.example.pharma.dto.Product;

public record ProductResponse(
     Long id,
     String name,
     String image,
     Double price,
     Double originalPrice,
     Boolean inStock,
     String category,
     String pharmacyName,
     Double pharmacyLatitude,
     Double pharmacyLongitude,
     Double pharmacyDistance
){}