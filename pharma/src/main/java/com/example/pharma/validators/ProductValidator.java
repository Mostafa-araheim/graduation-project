package com.example.pharma.validators;

import com.example.pharma.dto.Product.ProductFilter;
import com.example.pharma.exception.validation.ValidationException;
import org.springframework.stereotype.Component;

@Component
public class ProductValidator {

    public void validateFilter(ProductFilter filter) {
        if (filter.productName() != null && filter.productName().trim().isEmpty()) {
            throw new ValidationException("Product name filter cannot be empty");
        }
        if (filter.categoryName() != null && filter.categoryName().trim().isEmpty()) {
            throw new ValidationException("Category name filter cannot be empty");
        }
    }

    public void validateProductId(Long id) {
        if (id == null || id <= 0) {
            throw new ValidationException("Product id must be a positive number");
        }
    }
}

