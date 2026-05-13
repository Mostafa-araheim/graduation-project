package com.example.pharma.mapper.cart;

import com.example.pharma.dto.cart.response.CartItemResponse;
import com.example.pharma.model.cart.CartItem;
import com.example.pharma.model.entity.inventory.PharmacyProduct;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;

@Mapper(componentModel = "spring")
public interface CartItemMapper {

    @Mapping(target = "totalPrice", expression = "java(calculateTotal(cartItem))")
    @Mapping(target = "productImageUrl", source = "productImageUrl")
    @Mapping(target = "productName", source = "productName")
    CartItemResponse toDto(
            CartItem cartItem,
            String productImageUrl,
            String productName
    );
    default BigDecimal calculateTotal(CartItem cartItem) {
        if (cartItem == null || cartItem.getPricePerUnit() == null || cartItem.getQuantity() == null) {
            return BigDecimal.ZERO;
        }
        return cartItem.getPricePerUnit()
                .multiply(BigDecimal.valueOf(cartItem.getQuantity()));
    }

    @Mapping(target = "pharmacyProductId", source = "pharmacyProduct.pharmacyProductId")
    @Mapping(target = "quantity", expression = "java(1L)")
    @Mapping(target = "pricePerUnit", source = "pharmacyProduct.price")
    CartItem toEntity(PharmacyProduct pharmacyProduct);

    @Mapping(target = "pharmacyProductId", source = "pharmacyProduct.pharmacyProductId")
    @Mapping(target = "quantity", source = "quantity")
    @Mapping(target = "pricePerUnit", source = "pharmacyProduct.price")
    CartItem toEntity(PharmacyProduct pharmacyProduct, Long quantity);
}