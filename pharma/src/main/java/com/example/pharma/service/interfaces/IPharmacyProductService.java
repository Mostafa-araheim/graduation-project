package com.example.pharma.service.interfaces;

import com.example.pharma.dto.common.PageResponse;
import com.example.pharma.dto.pharmacyProduct.PharmacyProductFilter;
import com.example.pharma.dto.pharmacyProduct.pharmacyProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface IPharmacyProductService {
    PageResponse<pharmacyProductResponse> getPharmacyProducts(PharmacyProductFilter filter, Pageable pageable);
}
