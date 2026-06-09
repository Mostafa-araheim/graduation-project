package com.example.pharma.dto.order.response;

import java.math.BigDecimal;

public record CustomerOrderItemResponse(
        Long productId,
        String productName,
        Integer quantity,
        BigDecimal priceAtPurchase,
        BigDecimal subtotal
) {}
