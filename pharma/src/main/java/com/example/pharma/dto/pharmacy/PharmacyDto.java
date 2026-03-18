package com.example.pharma.dto.pharmacy;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalTime;

public record PharmacyDto(
        @JsonProperty("pharmacy_id") Long pharmacyId,
        @JsonProperty("name") String name,
        @JsonProperty("image_url") String imageUrl,
        @JsonProperty("rating") BigDecimal averageRating,
        @JsonProperty("distance_in_kilometers") Float distanceInKms,
        @JsonProperty("opening_time") LocalTime openingTime,
        @JsonProperty("closing_time") LocalTime closingTime,
        @JsonProperty("latitude") Double latitude,
        @JsonProperty("longitude") Double longitude,
        @JsonProperty("review_count") Long reviewCount,
        @JsonProperty("is_closed") Boolean isClosed,
        @JsonProperty("address") String address
        ) {
}
