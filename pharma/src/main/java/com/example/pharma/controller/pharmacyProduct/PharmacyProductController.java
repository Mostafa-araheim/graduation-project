package com.example.pharma.controller.pharmacyProduct;

import com.example.pharma.dto.pharmacyProduct.PharmacyProductFilter;
import com.example.pharma.dto.pharmacyProduct.pharmacyProductResponse;
import com.example.pharma.dto.common.ApiResponse;
import com.example.pharma.service.PharmacyProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/pharmacy-products")
@RequiredArgsConstructor
public class PharmacyProductController {

    private final PharmacyProductService pharmacyProductService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<pharmacyProductResponse>>> getPharmacyProducts(
            @Valid @ModelAttribute PharmacyProductFilter filter,
            Sort sort
    ) {
        List<pharmacyProductResponse> pharmacyProducts = pharmacyProductService.getPharmacyProducts(filter, sort);

        return ResponseEntity.ok(
                ApiResponse.success("Pharmacy products retrieved successfully", pharmacyProducts)
        );
    }
}

