package com.example.pharma.repository.Order;

import com.example.pharma.model.entity.order.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
    boolean existsBySourceCartId(Long sourceCartId);
}