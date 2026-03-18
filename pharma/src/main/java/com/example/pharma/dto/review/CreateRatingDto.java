package com.example.pharma.dto.review;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CreateRatingDto(@JsonProperty("rating") Integer rating, @JsonProperty("pharmacy_id") Long pharmacyId) {
}
