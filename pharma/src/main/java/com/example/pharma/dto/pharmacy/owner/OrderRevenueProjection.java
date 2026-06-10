package com.example.pharma.dto.pharmacy.owner;

import com.example.pharma.model.entity.order.OrderStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface OrderRevenueProjection {
    LocalDateTime getCreatedAtValue();
    BigDecimal getTotalPrice();
    OrderStatus getStatus();
}
