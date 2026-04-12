package com.example.pharma.dto.Product;

import com.example.pharma.model.entity.catalog.DosageForm;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

public record ProductFilter(
        String productName,
        String categoryName,
        @Enumerated(EnumType.STRING)
        DosageForm dosageForm
){}
