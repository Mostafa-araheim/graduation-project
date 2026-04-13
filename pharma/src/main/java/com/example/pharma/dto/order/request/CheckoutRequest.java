package com.example.pharma.dto.order.request;

import com.example.pharma.model.entity.order.DeliveryType;
import com.example.pharma.model.entity.order.PaymentMethod;
import jakarta.validation.constraints.NotNull;

public record CheckoutRequest(

        @NotNull(message = "Delivery type is required")
        DeliveryType deliveryType,

        @NotNull(message = "Payment method is required")
        PaymentMethod paymentMethod,

         Long deliveryAddressId
) {}