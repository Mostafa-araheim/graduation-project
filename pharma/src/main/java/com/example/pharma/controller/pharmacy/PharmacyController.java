package com.example.pharma.controller.pharmacy;

import com.example.pharma.dto.common.ApiResponse;
import com.example.pharma.dto.common.PageResponse;
import com.example.pharma.dto.pharmacy.CreatePharmacyRequest;
import com.example.pharma.dto.pharmacy.PharmacyDto;
import com.example.pharma.dto.pharmacy.PharmacyInfo;
import com.example.pharma.model.entity.catalog.Medicine;
import com.example.pharma.service.PharmacyService;
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
    public ApiResponse<PageResponse<PharmacyDto>> getPharmacies(@RequestParam(required = false) String name ,
                                                                @RequestParam(required = false) Float minRating,
                                                                @RequestParam(required = false) Boolean isOpen,
                                                                @PageableDefault(sort = "pharmacyId") Pageable pageable) {
        return ApiResponse.success("Pharmacies retrieved successfully", pharmacyService.getPharmacies(name, minRating, isOpen, pageable));
    }
    @GetMapping("/{pharmacyId}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<PharmacyInfo> getPharmacyInfo(@PathVariable Integer pharmacyId)
    {
        return ApiResponse.success("Pharmacy info fetched successfully",pharmacyService.getPharmacyInfo(pharmacyId));
    }
    @GetMapping("/{pharmacyId}/{categoryId}")
    public ApiResponse<PageResponse<Medicine>> getPharmacyMedicinesUnderACategory(@PathVariable Integer pharmacyId, @PathVariable Integer categoryId, @PageableDefault(sort = "medicineId") Pageable pageable)
    {
        return ApiResponse.success("Medicines returned successfully",pharmacyService.getPharmacyMedicinesUnderACategory(pharmacyId, categoryId, pageable));
    }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createBulk(@RequestBody List<CreatePharmacyRequest> createPharmacyRequestList)
    {
        pharmacyService.createPharmacies(createPharmacyRequestList);
    }


}
