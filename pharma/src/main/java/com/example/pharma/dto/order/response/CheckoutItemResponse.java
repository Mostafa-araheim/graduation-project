package com.example.pharma.dto.order.response;

import java.math.BigDecimal;

public record CheckoutItemResponse(
        Long productId,
        String productName,
        Long pharmacyProductId,
        Long quantity,
        BigDecimal unitPrice,
        BigDecimal subtotal
) {}
