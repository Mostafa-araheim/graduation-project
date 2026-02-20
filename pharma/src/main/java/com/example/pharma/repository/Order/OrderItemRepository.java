package com.example.pharma.repository.Order;

import com.example.pharma.model.entity.order.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {
    List<OrderItem> findByOrder_OrderId(Integer orderId);
}