package com.example.pharma.service;

import com.example.pharma.dto.Product.ProductFilter;
import com.example.pharma.dto.Product.ProductResponse;
import com.example.pharma.exception.validation.ValidationException;
import com.example.pharma.mapper.ProductMapper;
import com.example.pharma.model.entity.catalog.Product;
import com.example.pharma.repository.Catalog.ProductRepository;
import com.example.pharma.specification.ProductSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository ProductRepository;
    private final LocationService locationService;
    private final ProductMapper ProductMapper;
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("name", "category", "requiresPrescription", "dosageForm");

        public List<ProductResponse> getProducts(ProductFilter filter, Sort sort) {
            validateFilter(filter);
            validateSort(sort);

            Specification<Product> spec = ProductSpecification.buildFromFilter(filter);

            var Products = ProductRepository.findAll(spec, sort)
                    .stream()
                    .map(ProductMapper::toResponse)
                    .toList();
            return Products;
    }

    private void validateFilter(ProductFilter filter) {

        if (filter.productName() != null && filter.productName().trim().isEmpty()) {
            throw new ValidationException("Product name filter cannot be empty");
        }
        if (filter.categoryName() != null && filter.categoryName().trim().isEmpty()) {
            throw new ValidationException("Category name filter cannot be empty");
        }
    }

    private void validateSort(Sort sort) {
        if (sort == null || sort.isUnsorted()) {
            return;
        }
        List<String> invalidFields = sort.stream()
                .map(Sort.Order::getProperty)
                .filter(property -> !ALLOWED_SORT_FIELDS.contains(property))
                .toList();

        if (!invalidFields.isEmpty()) {
            throw new ValidationException(
                    "Invalid sort fields: " + invalidFields + ". Allowed fields: " + ALLOWED_SORT_FIELDS
            );
        }
    }

    private void validateProductId(Long id) {
        if (id == null || id <= 0) {
            throw new ValidationException("Product id must be a positive number");
        }
        if (!ProductRepository.existsById(id)) {
            throw new ValidationException("Product with id " + id + " does not exist");
        }
    }
}