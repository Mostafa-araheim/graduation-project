package com.example.pharma.dto.Product;

import com.example.pharma.model.entity.catalog.DosageForm;

public record ProductResponse(
     Long id,
     String name,
     String description,
     Boolean requiresPrescription,
     DosageForm dosageForm,
     String strength,
     String manufacturer,
     String category
){}

