package com.example.pharma.mapper.pharmacy;

import com.example.pharma.dto.order.response.OwnerOrderItemResponse;
import com.example.pharma.dto.order.response.OwnerOrderResponse;
import com.example.pharma.model.entity.order.Order;
import com.example.pharma.model.entity.order.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OwnerOrderMapper {

    @Mapping(source = "customer.userId", target = "customerId")
    @Mapping(source = "customer.user.name", target = "customerName")
    @Mapping(source = "pharmacy.pharmacyId", target = "pharmacyId")
    OwnerOrderResponse toResponse(Order order);

    @Mapping(source = "product.productId", target = "productId")
    @Mapping(source = "product.name", target = "productName")
    OwnerOrderItemResponse toItemResponse(OrderItem item);
}