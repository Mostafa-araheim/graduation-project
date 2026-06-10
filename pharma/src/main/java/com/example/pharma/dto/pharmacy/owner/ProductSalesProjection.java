package com.example.pharma.dto.pharmacy.owner;

import java.math.BigDecimal;

public interface ProductSalesProjection {
    Long getProductId();
    String getProductName();
    Long getQuantitySold();
    BigDecimal getTotalRevenue();
}
