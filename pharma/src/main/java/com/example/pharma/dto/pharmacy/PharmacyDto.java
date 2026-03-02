package com.example.pharma.dto.pharmacy;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalTime;

public record PharmacyDto(
        @JsonProperty("pharmacy_id") Integer pharmacyId,
        @JsonProperty("name") String name,
        @JsonProperty("image_url") String imageUrl,
        @JsonProperty("total_rating") Float totalRating,
        @JsonProperty("distance_in_kilometers") Float distanceInKms,
        @JsonProperty("opening_time") LocalTime openingTime,
        @JsonProperty("closing_time") LocalTime closingTime
        ) {
}
