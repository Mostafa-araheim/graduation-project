package com.example.pharma.dto.order.response;

import com.example.pharma.model.entity.order.DeliveryType;
import com.example.pharma.model.entity.order.OrderStatus;
import com.example.pharma.model.entity.order.PaymentMethod;

import java.math.BigDecimal;
import java.util.List;

public record OwnerOrderResponse(
        Long orderId,
        Long customerId,
        String customerName,
        Long pharmacyId,
        BigDecimal totalPrice,
        DeliveryType deliveryType,
        PaymentMethod paymentMethod,
        OrderStatus status,
        List<OwnerOrderItemResponse> items
) {
}