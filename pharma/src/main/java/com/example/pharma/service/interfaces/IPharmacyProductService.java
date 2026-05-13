package com.example.pharma.service.interfaces;

import com.example.pharma.dto.common.PageResponse;
import com.example.pharma.dto.pharmacyProduct.PharmacyProductDto;
import com.example.pharma.dto.pharmacyProduct.PharmacyProductFilter;
import com.example.pharma.dto.pharmacyProduct.pharmacyProductResponse;
import org.springframework.data.domain.Pageable;

public interface IPharmacyProductService {
    PageResponse<pharmacyProductResponse> getPharmacyProducts(PharmacyProductFilter filter, Pageable pageable);
    PharmacyProductDto getPharmacyProductById(Long pharmacyProductId);
}
