package com.example.pharma.model.cart;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartItem {
    private Long productId;
    private Integer quantity;
    private BigDecimal pricePerUnit;
    private Instant addedAt;
}
