package com.example.pharma.repository.P2P;

import com.example.pharma.model.entity.P2P.ProductReservation;
import com.example.pharma.model.entity.P2P.ReservationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ProductReservationRepository extends JpaRepository<ProductReservation, Long> {
    
    @EntityGraph(attributePaths = {"user", "user.user"})
    List<ProductReservation> findByProduct_ProductIdAndStatus(Long productId, ReservationStatus status);

    @EntityGraph(attributePaths = {"product"})
    Page<ProductReservation> findByUserId(Long userId, Pageable pageable);

    boolean existsByUserIdAndProduct_ProductIdAndStatusIn(
            Long userId,
            Long productId,
            List<ReservationStatus> statuses
    );

    @Modifying
    @Query("UPDATE ProductReservation p SET p.status = :expiredStatus WHERE p.status IN (:statuses) AND p.createdAt.value < :thresholdDate")
    int expireOldReservations(
            @Param("expiredStatus") ReservationStatus expiredStatus,
            @Param("statuses") List<ReservationStatus> statuses,
            @Param("thresholdDate") LocalDateTime thresholdDate);
}
