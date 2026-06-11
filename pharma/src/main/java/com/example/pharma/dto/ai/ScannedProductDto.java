package com.example.pharma.dto.ai;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ScannedProductDto(
        @JsonProperty("product_id")            Long productId,
        @JsonProperty("name")                  String name,
        @JsonProperty("description")           String description,
        @JsonProperty("requires_prescription") Boolean requiresPrescription,
        @JsonProperty("dosage_form")           String dosageForm,
        @JsonProperty("strength")              String strength,
        @JsonProperty("manufacturer")          String manufacturer,
        @JsonProperty("image_url")             String imageUrl,
        @JsonProperty("category")             String category,
        @JsonProperty("brand")                String brand
) {}
