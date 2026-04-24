package com.example.pharma.dto.order.response;

import java.math.BigDecimal;

public record OwnerOrderItemResponse(
        Long orderItemId,
        Long productId,
        String productName,
        Integer quantity,
        BigDecimal priceAtPurchase,
        BigDecimal subtotal
) {
}
