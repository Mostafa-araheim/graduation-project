package com.example.pharma.dto.Product;

import jakarta.validation.constraints.DecimalMin;

public record ProductFilter(
     String categoryName,
     Double userLatitude,
     Double userLongitude,

    @DecimalMin(value = "1.0", message = "Distance cannot be negative or zero")
     Double maxDistanceKm,

    @DecimalMin(value = "0.0", message = "Minimum price cannot be negative")
     Double minPrice,

    @DecimalMin(value = "0.0", message = "Maximum price cannot be negative")
     Double maxPrice,

     Boolean inStock
){}
