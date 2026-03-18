package com.example.pharma.dto.pharmacy;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;

public record PharmacySearchFilter(

        @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
        String name,

        @DecimalMin(value = "0.0", message = "Minimum rating cannot be negative")
        @DecimalMax(value = "5.0", message = "Rating cannot be greater than 5")
        Float minRating,

        Boolean isOpen,

        @DecimalMin(value = "-90.0", message = "Latitude must be >= -90")
        @DecimalMax(value = "90.0", message = "Latitude must be <= 90")
        Double latitude,

        @DecimalMin(value = "-180.0", message = "Longitude must be >= -180")
        @DecimalMax(value = "180.0", message = "Longitude must be <= 180")
        Double longitude

) {}