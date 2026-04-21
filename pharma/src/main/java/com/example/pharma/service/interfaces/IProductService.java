package com.example.pharma.service.interfaces;

import com.example.pharma.dto.Product.ProductFilter;
import com.example.pharma.dto.Product.ProductResponse;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface IProductService {
    List<ProductResponse> getProducts(ProductFilter filter, Sort sort);
    ProductResponse getProductById(Long id);
}
