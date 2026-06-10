package com.example.pharma.repository.Order;

import com.example.pharma.model.entity.order.Order;
import com.example.pharma.model.entity.order.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.pharma.dto.pharmacy.owner.OrderRevenueProjection;
import java.time.LocalDateTime;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    boolean existsBySourceCartId(Long sourceCartId);
    List<Order> findByPharmacy_PharmacyId(Long pharmacyId);
    Page<Order> findByPharmacy_PharmacyId(Long pharmacyId, Pageable pageable);

    Page<Order> findByCustomer_UserId(Long customerId, Pageable pageable);
    Optional<Order> findByOrderIdAndCustomer_UserId(Long orderId, Long customerId);

    Long countByPharmacy_PharmacyId(Long pharmacyId);

    Long countByPharmacy_PharmacyIdAndStatus(Long pharmacyId, OrderStatus status);

    @Query("SELECT COALESCE(SUM(o.totalPrice), 0) FROM Order o WHERE o.pharmacy.pharmacyId = :pharmacyId AND o.status = :status")
    BigDecimal sumRevenueByPharmacyIdAndStatus(@Param("pharmacyId") Long pharmacyId, @Param("status") OrderStatus status);

    @Query("SELECT COALESCE(COUNT(o), 0) FROM Order o WHERE o.pharmacy.owner.userId = :ownerUserId")
    Long countOrdersByOwner(@Param("ownerUserId") Long ownerUserId);

    @Query("SELECT COALESCE(SUM(o.totalPrice), 0) FROM Order o WHERE o.pharmacy.owner.userId = :ownerUserId AND o.status = :status")
    BigDecimal sumRevenueByOwnerAndStatus(@Param("ownerUserId") Long ownerUserId, @Param("status") OrderStatus status);

    @Query("SELECT o.createdAt.value as createdAtValue, o.totalPrice as totalPrice, o.status as status " +
           "FROM Order o " +
           "WHERE o.pharmacy.pharmacyId = :pharmacyId " +
           "AND o.createdAt.value >= :startDate")
    List<OrderRevenueProjection> findRevenueData(
            @Param("pharmacyId") Long pharmacyId,
            @Param("startDate") LocalDateTime startDate
    );

    @Query("SELECT o.createdAt.value as createdAtValue, o.totalPrice as totalPrice, o.status as status " +
           "FROM Order o " +
           "WHERE o.pharmacy.owner.userId = :ownerUserId " +
           "AND o.createdAt.value >= :startDate")
    List<OrderRevenueProjection> findRevenueDataForOwner(
            @Param("ownerUserId") Long ownerUserId,
            @Param("startDate") LocalDateTime startDate
    );
}