package com.example.pharma.dto.ai;

import com.example.pharma.model.entity.catalog.DosageForm;
import com.fasterxml.jackson.annotation.JsonProperty;

public record PredictionDto(
        @JsonProperty("drug_name") String drugName,
        @JsonProperty("form") DosageForm form,
        @JsonProperty("category") String category,
        @JsonProperty("dosage") String dosage,
        @JsonProperty("frequency") String frequency
) {}
