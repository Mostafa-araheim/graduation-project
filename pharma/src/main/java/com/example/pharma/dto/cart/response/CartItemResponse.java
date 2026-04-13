package com.example.pharma.dto.cart.response;

import java.math.BigDecimal;

public record CartItemResponse(
        Long pharmacyProductId,
        Long quantity,
        BigDecimal pricePerUnit,
        BigDecimal totalPrice
) {}
