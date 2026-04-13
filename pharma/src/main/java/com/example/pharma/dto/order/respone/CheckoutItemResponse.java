package com.example.pharma.dto.order.respone;

import java.math.BigDecimal;

public record CheckoutItemResponse(
        Long productId,
        String productName,
        Long pharmacyProductId,
        Long quantity,
        BigDecimal unitPrice,
        BigDecimal subtotal
) {}
