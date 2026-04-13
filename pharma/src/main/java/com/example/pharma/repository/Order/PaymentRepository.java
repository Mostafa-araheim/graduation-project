package com.example.pharma.repository.Order;

import com.example.pharma.model.entity.order.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByProviderPaymentIntentId(String providerPaymentIntentId);

}
