package com.example.pharma.mapper;

import com.example.pharma.dto.Product.ProductResponse;
import com.example.pharma.model.entity.catalog.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(source = "productId", target = "id")
    @Mapping(source = "category.categoryName", target = "category")
    ProductResponse toResponse(Product product);
    List<ProductResponse> toResponseList(List<Product> products);

}
