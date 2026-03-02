package com.example.pharma.dto.pharmacy;

import com.example.pharma.dto.review.ReviewDto;
import com.example.pharma.model.entity.catalog.Category;
import com.example.pharma.model.entity.pharmacy.PharmacyAddress;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record PharmacyInfo(@JsonProperty("categories") List<Category> categories,
                           @JsonProperty("pharmacy_address") PharmacyAddress pharmacyAddress,
                           @JsonProperty("pharmacy_dto") PharmacyDto pharmacyDto,
                           @JsonProperty("reviews")List<ReviewDto> reviews) {
}
