package com.example.pharma.repository.Order;

import com.example.pharma.model.entity.order.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByCustomer_UserId(Long userId);
    List<Order> findByPharmacy_PharmacyId(Long pharmacyId);
}