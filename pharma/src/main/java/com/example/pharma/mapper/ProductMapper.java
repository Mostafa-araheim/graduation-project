package com.example.pharma.mapper;

import com.example.pharma.dto.Product.ProductResponse;
import com.example.pharma.model.entity.catalog.Product;
import com.example.pharma.model.entity.inventory.AvailabilityStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(source = "productId", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "category.categoryName", target = "category")
    @Mapping(target = "image", ignore = true)
    @Mapping(target = "price", ignore = true)
    @Mapping(target = "originalPrice", ignore = true)
    @Mapping(target = "inStock", expression = "java(isInStock(product))")
    @Mapping(target = "pharmacyName", expression = "java(firstPharmacyName(product))")
    @Mapping(target = "pharmacyDistance", ignore = true)
    @Mapping(target = "pharmacyLatitude", expression = "java(firstPharmacyLatitude(product))")
    @Mapping(target = "pharmacyLongitude", expression = "java(firstPharmacyLongitude(product))")
    ProductResponse toResponse(Product product);
    List<ProductResponse> toResponseList(List<Product> products);

    default boolean isInStock(Product product) {
        return product.getPharmacyProducts() != null &&
                product.getPharmacyProducts().stream()
                        .anyMatch(record -> AvailabilityStatus.Available.equals(record.getAvailabilityStatus()));
    }

    default String firstPharmacyName(Product product) {
        if (product.getPharmacyProducts() == null || product.getPharmacyProducts().isEmpty()) {
            return null;
        }
        var record = product.getPharmacyProducts().get(0);
        return record.getInventory() != null && record.getInventory().getPharmacy() != null
                ? record.getInventory().getPharmacy().getName()
                : null;
    }

    default Double firstPharmacyLatitude(Product product) {
        if (product.getPharmacyProducts() == null || product.getPharmacyProducts().isEmpty()) {
            return null;
        }
        var record = product.getPharmacyProducts().get(0);
        return record.getInventory() != null && record.getInventory().getPharmacy() != null
                ? record.getInventory().getPharmacy().getLatitude()
                : null;
    }

    default Double firstPharmacyLongitude(Product product) {
        if (product.getPharmacyProducts() == null || product.getPharmacyProducts().isEmpty()) {
            return null;
        }
        var record = product.getPharmacyProducts().get(0);
        return record.getInventory() != null && record.getInventory().getPharmacy() != null
                ? record.getInventory().getPharmacy().getLongitude()
                : null;
    }
}
