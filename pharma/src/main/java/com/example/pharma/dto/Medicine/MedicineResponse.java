package com.example.pharma.dto.Medicine;

public record MedicineResponse(
     Long id,
     String name,
     String image,
     Double price,
     Double originalPrice,
     Boolean inStock,
     String category,
     String pharmacyName,
     Double pharmacyDistance
){}