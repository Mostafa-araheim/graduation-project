package com.example.pharma.mapper;

import com.example.pharma.dto.pharmacyProduct.pharmacyProductResponse;
import com.example.pharma.model.entity.inventory.AvailabilityStatus;
import com.example.pharma.model.entity.inventory.PharmacyProduct;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PharmacyProductMapper {

    @Mapping(source = "pharmacyProductId", target = "id")
    @Mapping(source = "product.productId", target = "productId")
    @Mapping(source = "product.name", target = "productName")
    @Mapping(source = "product.category.categoryName", target = "category")
    @Mapping(source = "inventory.pharmacy.pharmacyId", target = "pharmacyId")
    @Mapping(source = "inventory.pharmacy.name", target = "pharmacyName")
    @Mapping(source = "inventory.pharmacy.latitude", target = "pharmacyLatitude")
    @Mapping(source = "inventory.pharmacy.longitude", target = "pharmacyLongitude")
    @Mapping(target = "productImage", ignore = true) // Set up accordingly
    @Mapping(target = "originalPrice", ignore = true) // Define logic if any
    @Mapping(target = "pharmacyDistance", ignore = true) // Will be set post-query
    @Mapping(target = "inStock", expression = "java(pharmacyProduct.getAvailabilityStatus() == com.example.pharma.model.entity.inventory.AvailabilityStatus.Available)")
    pharmacyProductResponse toResponse(PharmacyProduct pharmacyProduct);

    List<pharmacyProductResponse> toResponseList(List<PharmacyProduct> pharmacyProducts);
}

