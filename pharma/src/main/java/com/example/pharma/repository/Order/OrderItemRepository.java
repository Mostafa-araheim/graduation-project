package com.example.pharma.repository.Order;

import com.example.pharma.model.entity.order.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.pharma.dto.pharmacy.owner.ProductSalesProjection;
import com.example.pharma.model.entity.order.OrderStatus;
import java.time.LocalDateTime;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findByOrder_OrderId(Long orderId);

    @Query("SELECT oi.product.productId as productId, oi.product.name as productName, " +
           "SUM(oi.quantity) as quantitySold, SUM(oi.subtotal) as totalRevenue " +
           "FROM OrderItem oi " +
           "WHERE oi.order.pharmacy.pharmacyId = :pharmacyId " +
           "AND oi.order.status IN (:statuses) " +
           "AND oi.order.createdAt.value >= :startDate " +
           "GROUP BY oi.product.productId, oi.product.name " +
           "ORDER BY SUM(oi.quantity) DESC")
    List<ProductSalesProjection> findBestSellers(
            @Param("pharmacyId") Long pharmacyId,
            @Param("statuses") List<OrderStatus> statuses,
            @Param("startDate") LocalDateTime startDate,
            Pageable pageable
    );

    @Query("SELECT oi.product.productId as productId, oi.product.name as productName, " +
           "SUM(oi.quantity) as quantitySold, SUM(oi.subtotal) as totalRevenue " +
           "FROM OrderItem oi " +
           "WHERE oi.order.pharmacy.owner.userId = :ownerUserId " +
           "AND oi.order.status IN (:statuses) " +
           "AND oi.order.createdAt.value >= :startDate " +
           "GROUP BY oi.product.productId, oi.product.name " +
           "ORDER BY SUM(oi.quantity) DESC")
    List<ProductSalesProjection> findBestSellersForOwner(
            @Param("ownerUserId") Long ownerUserId,
            @Param("statuses") List<OrderStatus> statuses,
            @Param("startDate") LocalDateTime startDate,
            Pageable pageable
    );
}