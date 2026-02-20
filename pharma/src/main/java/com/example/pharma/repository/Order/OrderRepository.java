package com.example.pharma.repository.Order;

import com.example.pharma.model.entity.order.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Integer> {
    List<Order> findByCustomer_UserId(Integer userId);
    List<Order> findByPharmacy_PharmacyId(Integer pharmacyId);
}