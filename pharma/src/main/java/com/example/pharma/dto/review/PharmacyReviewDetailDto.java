package com.example.pharma.dto.review;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

public record PharmacyReviewDetailDto(
        @JsonProperty("review_id")    Long reviewId,
        @JsonProperty("customer_name") String customerName,
        @JsonProperty("comment")       String comment,
        @JsonProperty("created_at")    LocalDateTime createdAt
) {}
