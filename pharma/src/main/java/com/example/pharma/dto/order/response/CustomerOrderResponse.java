package com.example.pharma.dto.order.response;

import com.example.pharma.dto.user.AddressDto;
import com.example.pharma.model.entity.order.DeliveryType;
import com.example.pharma.model.entity.order.OrderStatus;
import com.example.pharma.model.entity.order.PaymentMethod;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record CustomerOrderResponse(
        Long orderId,
        Long pharmacyId,
        String pharmacyName,
        BigDecimal totalPrice,
        DeliveryType deliveryType,
        PaymentMethod paymentMethod,
        OrderStatus status,
        LocalDateTime createdAt,
        AddressDto deliveryAddress,
        List<CustomerOrderItemResponse> items
) {}
