package com.example.pharma.model.cart;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartItem {

    private Long pharmacyProductId;

    private Long quantity;

    private BigDecimal pricePerUnit;
}
