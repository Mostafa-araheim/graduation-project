package com.example.pharma.service;

import com.example.pharma.dto.Product.ProductFilter;
import com.example.pharma.dto.Product.ProductResponse;
import com.example.pharma.exception.validation.ValidationException;
import com.example.pharma.mapper.ProductMapper;
import com.example.pharma.model.entity.catalog.Product;
import com.example.pharma.repository.Catalog.ProductRepository;
import com.example.pharma.specification.ProductSpecification;
import com.example.pharma.validators.ProductValidator;
import com.example.pharma.validators.SortValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository ProductRepository;
    private final ProductMapper ProductMapper;
    private final ProductValidator validator;
    private static final Map<String, String> SORT_FIELD_MAPPING = Map.of(
            "name", "name",
            "category", "category",
            "requiresPrescription", "requiresPrescription",
            "dosageForm", "dosageForm"
    );

        public List<ProductResponse> getProducts(ProductFilter filter, Sort sort) {
            validator.validateFilter(filter);
            Sort mappedSort = SortValidator.validateAndMap(sort, SORT_FIELD_MAPPING);

            Specification<Product> spec = ProductSpecification.buildFromFilter(filter);

            var Products = ProductRepository.findAll(spec, mappedSort)
                    .stream()
                    .map(ProductMapper::toResponse)
                    .toList();
            return Products;
    }

    public ProductResponse getProductById(Long id) {
        validator.validateProductId(id);
        Product product = ProductRepository.findById(id)
                .orElseThrow(()-> new ValidationException("Product with id " + id + " does not exist"));
        return ProductMapper.toResponse(product);
    }
}