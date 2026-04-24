package com.example.pharma.repository.Order;

import com.example.pharma.model.entity.order.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    boolean existsBySourceCartId(Long sourceCartId);
    List<Order> findByPharmacy_PharmacyId(Long pharmacyId);
    Page<Order> findByPharmacy_PharmacyId(Long pharmacyId, Pageable pageable);
}