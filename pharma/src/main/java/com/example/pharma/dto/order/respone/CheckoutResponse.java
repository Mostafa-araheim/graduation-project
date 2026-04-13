package com.example.pharma.dto.order.respone;

import com.example.pharma.model.entity.order.DeliveryType;
import com.example.pharma.model.entity.order.OrderStatus;
import com.example.pharma.model.entity.order.PaymentMethod;
import com.example.pharma.model.entity.order.PaymentStatus;

import java.math.BigDecimal;
import java.util.List;

public record CheckoutResponse(
        Long orderId,
        Long cartId,
        Long pharmacyId,
        DeliveryType deliveryType,
        PaymentMethod paymentMethod,
        OrderStatus orderStatus,
        BigDecimal totalPrice,
        String currency,
        boolean paymentRequired,
        PaymentStatus paymentStatus,
        String clientSecret,
        List<CheckoutItemResponse> items
) {}