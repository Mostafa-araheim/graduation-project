package com.example.pharma.dto.pharmacy.owner;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

public record OwnerProfileDto(
        @JsonProperty("user_id")       Long userId,
        @JsonProperty("name")          String name,
        @JsonProperty("email")         String email,
        @JsonProperty("phone")         String phone,
        @JsonProperty("image_url")     String imageUrl,
        @JsonProperty("member_since")  LocalDateTime memberSince,
        @JsonProperty("total_pharmacies") Long totalPharmacies
) {}
