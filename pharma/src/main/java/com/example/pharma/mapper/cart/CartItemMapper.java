package com.example.pharma.mapper.cart;

import com.example.pharma.dto.cart.response.CartItemResponse;
import com.example.pharma.model.cart.CartItem;
import com.example.pharma.model.entity.inventory.InventoryRecord;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;

@Mapper(componentModel = "spring")
public interface CartItemMapper {

    @Mapping(target = "totalPrice", expression = "java(calculateTotal(cartItem))")
    CartItemResponse toDto(CartItem cartItem);

    default BigDecimal calculateTotal(CartItem cartItem) {
        if (cartItem == null || cartItem.getPricePerUnit() == null || cartItem.getQuantity() == null) {
            return BigDecimal.ZERO;
        }
        return cartItem.getPricePerUnit().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
    }


    @Mapping(target = "inventoryRecordId", source = "inventoryRecord.inventoryRecordId")
    @Mapping(target = "quantity", expression = "java(1L)")
    @Mapping(target = "pricePerUnit", source = "inventoryRecord.price")
    CartItem toEntity(InventoryRecord inventoryRecord);

    @Mapping(target = "inventoryRecordId", source = "inventoryRecord.inventoryRecordId")
    @Mapping(target = "quantity", source = "quantity")
    @Mapping(target = "pricePerUnit", source = "inventoryRecord.price")
    CartItem toEntity(InventoryRecord inventoryRecord, Long quantity);

}
