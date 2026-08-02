package com.example.pharma.mapper;

import com.example.pharma.dto.pharmacyProduct.PharmacyProductDto;
import com.example.pharma.dto.pharmacyProduct.pharmacyProductResponse;
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
    @Mapping(source = "pharmacy.pharmacyId", target = "pharmacyId")
    @Mapping(source = "pharmacy.name", target = "pharmacyName")
    @Mapping(source = "pharmacy.latitude", target = "pharmacyLatitude")
    @Mapping(source = "pharmacy.longitude", target = "pharmacyLongitude")
    @Mapping(source = "product.imageUrl", target = "productImage") // تأكد أن الاسم في Product.java هو imageUrl (لو كان image غيّر المصدر لـ product.image)
    @Mapping(target = "originalPrice", ignore = true)
    @Mapping(target = "pharmacyDistance", ignore = true)
    @Mapping(target = "inStock", expression = "java(pharmacyProduct.getAvailabilityStatus() != null && pharmacyProduct.getAvailabilityStatus() == com.example.pharma.model.entity.inventory.AvailabilityStatus.Available)")
    pharmacyProductResponse toResponse(PharmacyProduct pharmacyProduct);

    List<pharmacyProductResponse> toResponseList(List<PharmacyProduct> pharmacyProducts);

    @Mapping(source = "product.productId",            target = "productId")
    @Mapping(source = "product.name",                 target = "productName")
    @Mapping(source = "product.description",          target = "description")
    @Mapping(source = "product.requiresPrescription", target = "requiresPrescription")
    @Mapping(source = "product.dosageForm",           target = "dosageForm")
    @Mapping(source = "product.strength",             target = "strength")
    @Mapping(source = "product.manufacturer",         target = "manufacturer")
    @Mapping(source = "product.category.categoryId",  target = "categoryId")
    @Mapping(source = "product.category.categoryName", target = "categoryName")
    @Mapping(source = "product.imageUrl",             target = "productImage") // تم التأكد من المابينج للصورة
    @Mapping(source = "product.brand.brandId",        target = "brandId")
    @Mapping(source = "product.brand.brandName",      target = "brandName")
    @Mapping(source = "pharmacy.name",                target = "pharmacyName") // تصحيح: الربط المباشر مع pharmacy بدلاً من inventory
    @Mapping(target = "inStock", expression = "java(pharmacyProduct.getAvailabilityStatus() != null && pharmacyProduct.getAvailabilityStatus() == com.example.pharma.model.entity.inventory.AvailabilityStatus.Available)") // تصحيح: إضافة حماية ضد null
    PharmacyProductDto toPharmacyProductDto(PharmacyProduct pharmacyProduct);
}