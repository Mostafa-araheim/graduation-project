package com.example.pharma.dto.ai;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.List;

public record NearbyPharmacyResponse(
        @JsonProperty("pharmacy_id")              Long pharmacyId,
        @JsonProperty("pharmacy_name")            String pharmacyName,
        @JsonProperty("pharmacy_image")           String pharmacyImage,
        @JsonProperty("pharmacy_latitude")        Double pharmacyLatitude,
        @JsonProperty("pharmacy_longitude")       Double pharmacyLongitude,
        @JsonProperty("distance_km")              Double distanceKm,
        @JsonProperty("average_rating")           java.math.BigDecimal averageRating,
        @JsonProperty("is_open")                  Boolean isOpen,
        @JsonProperty("available_medicines")      List<PrescriptionMedicineOption> availableMedicines,
        @JsonProperty("available_count")          int availableCount,
        @JsonProperty("total_medicines_requested") int totalMedicinesRequested,
        @JsonProperty("total_price")              BigDecimal totalPrice
) {}
