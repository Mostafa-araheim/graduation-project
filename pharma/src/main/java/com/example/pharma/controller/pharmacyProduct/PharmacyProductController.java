package com.example.pharma.controller.pharmacyProduct;

import com.example.pharma.dto.common.PageResponse;
import com.example.pharma.dto.pharmacyProduct.PharmacyProductFilter;
import com.example.pharma.dto.pharmacyProduct.pharmacyProductResponse;
import com.example.pharma.dto.common.ApiResponse;
import com.example.pharma.service.interfaces.IPharmacyProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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

    private final IPharmacyProductService pharmacyProductService;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<pharmacyProductResponse>>> getPharmacyProducts(
            @Valid @ModelAttribute PharmacyProductFilter filter,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        PageResponse<pharmacyProductResponse> pharmacyProducts = pharmacyProductService.getPharmacyProducts(filter, pageable);

        return ResponseEntity.ok(
                ApiResponse.success("Pharmacy products retrieved successfully", pharmacyProducts)
        );
    }
}
