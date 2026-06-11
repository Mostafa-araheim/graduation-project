package com.example.pharma.dto.ai;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record PrescriptionScanResult(
        @JsonProperty("scanned_medicines") List<ScannedProductDto> scannedMedicines,
        @JsonProperty("nearby_pharmacies") List<NearbyPharmacyResponse> nearbyPharmacies
) {}
