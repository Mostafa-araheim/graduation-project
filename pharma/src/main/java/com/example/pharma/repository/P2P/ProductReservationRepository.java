package com.example.pharma.repository.P2P;

import com.example.pharma.model.entity.P2P.ProductReservation;
import com.example.pharma.model.entity.P2P.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductReservationRepository extends JpaRepository<ProductReservation, Long> {
    List<ProductReservation> findByProduct_ProductIdAndStatus(Long productId, ReservationStatus status);
}
