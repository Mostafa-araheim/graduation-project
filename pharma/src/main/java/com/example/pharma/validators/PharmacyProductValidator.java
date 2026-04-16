package com.example.pharma.validators;

import com.example.pharma.dto.pharmacyProduct.PharmacyProductFilter;
import com.example.pharma.exception.validation.ValidationException;
import org.springframework.stereotype.Component;

@Component
public class PharmacyProductValidator {

    public void validateFilter(PharmacyProductFilter filter) {
        if (filter.productId() != null && filter.productId() <= 0) {
            throw new ValidationException("Product id must be a positive number");
        }
        boolean hasProductId = filter.productId() != null;
//      if ((!hasProductId && filter.productName() == null || filter.productName().trim().isEmpty()) &&
//          (!hasProductId && filter.categoryName() == null || filter.categoryName().trim().isEmpty())){
//            throw new ValidationException("at least one of product name and category names must be filled");
//      }
        if (filter.minPrice() != null && filter.maxPrice() != null
                && filter.minPrice() > filter.maxPrice()) {
            throw new ValidationException("Minimum price cannot be greater than maximum price");
        }
        if (filter.maxDistanceKm() != null && filter.maxDistanceKm() < 0) {
            throw new ValidationException("Distance cannot be negative");
        }
        if (filter.maxDistanceKm() != null &&
                (filter.userLatitude() == null || filter.userLongitude() == null)) {
            throw new ValidationException("Latitude and longitude are required when filtering by distance");
        }
    }
}

