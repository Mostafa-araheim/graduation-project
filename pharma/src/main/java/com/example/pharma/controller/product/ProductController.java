package com.example.pharma.controller.product;

import com.example.pharma.dto.Product.ProductFilter;
import com.example.pharma.dto.Product.ProductResponse;
import com.example.pharma.dto.common.ApiResponse;
import com.example.pharma.model.entity.catalog.Product;
import com.example.pharma.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/Products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getProducts(
            @Valid @ModelAttribute ProductFilter filter,
            Sort sort
    ) {
        List<ProductResponse> Products = productService.getProducts(filter, sort);

        return ResponseEntity.ok(
                ApiResponse.success("Products retrieved successfully", Products)
        );
    }

//    @GetMapping("/{id}")
//    public ResponseEntity<ApiResponse<ProductResponse>> getProductById(@PathVariable Long id) {
//        ProductResponse product = productService.getProductById(id);
//        return ResponseEntity.ok(ApiResponse.success("Product retrieved successfully", product));
//    }
}
