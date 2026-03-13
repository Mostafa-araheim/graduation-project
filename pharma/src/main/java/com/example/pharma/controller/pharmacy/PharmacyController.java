package com.example.pharma.controller.pharmacy;

import com.example.pharma.dto.common.ApiResponse;
import com.example.pharma.dto.common.PageResponse;
import com.example.pharma.dto.pharmacy.CreatePharmacyRequest;
import com.example.pharma.dto.pharmacy.PharmacyDto;
import com.example.pharma.dto.pharmacy.PharmacyInfo;
import com.example.pharma.dto.pharmacy.PharmacySearchFilter;
import com.example.pharma.model.entity.catalog.Product;
import com.example.pharma.service.PharmacyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/pharmacies")
@RequiredArgsConstructor
public class PharmacyController {
    private final PharmacyService pharmacyService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<PageResponse<PharmacyDto>> getPharmacies(@Valid @ModelAttribute PharmacySearchFilter pharmacySearchFilter,
                                                                @PageableDefault(sort = "pharmacyId") Pageable pageable) {
        return ApiResponse.success("Pharmacies retrieved successfully", pharmacyService
                .getPharmacies(
                        pharmacySearchFilter.name(),
                        pharmacySearchFilter.minRating(),
                        pharmacySearchFilter.isOpen(),
                        pharmacySearchFilter.latitude(),
                        pharmacySearchFilter.longitude(),
                        pharmacySearchFilter.maxDistanceKm(),
                        pageable
                )
        );
    }

    @GetMapping("/{pharmacyId}")
    @ResponseStatus(HttpStatus.OK)


    public ApiResponse<PharmacyInfo> getPharmacyInfo(@PathVariable Long pharmacyId)
    {
        return ApiResponse.success("Pharmacy info fetched successfully",pharmacyService.getPharmacyInfo(pharmacyId));
    }
    @GetMapping("/{pharmacyId}/{categoryId}")
    public ApiResponse<PageResponse<Product>> getPharmacyMedicinesUnderACategory(@PathVariable Long pharmacyId, @PathVariable Long categoryId, @PageableDefault(sort = "medicineId") Pageable pageable)
    {
        return ApiResponse.success("Medicines returned successfully",pharmacyService.getPharmacyMedicinesUnderACategory(pharmacyId, categoryId, pageable));

    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createBulk(@RequestBody List<CreatePharmacyRequest> createPharmacyRequestList) {
        pharmacyService.createPharmacies(createPharmacyRequestList);
    }


}
