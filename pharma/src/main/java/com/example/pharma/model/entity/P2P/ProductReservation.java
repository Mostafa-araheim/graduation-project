package com.example.pharma.model.entity.P2P;

import com.example.pharma.model.entity.catalog.Product;
import com.example.pharma.model.entity.core.CreatedAtColumn;
import com.example.pharma.model.entity.core.CustomerProfile;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "product_reservation", indexes = {
    @Index(name = "idx_res_prod_status", columnList = "product_id, status")
})
public class ProductReservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reservationId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private CustomerProfile customerProfile;

    @Column(name = "user_id", insertable = false, updatable = false, nullable = false)
    private Long userId;

    private String productName;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "product_id", insertable = false, updatable = false,nullable = false)
    private Long productId;

    @Enumerated(EnumType.STRING)
    private ReservationStatus status = ReservationStatus.PENDING;
    @Embedded
    private CreatedAtColumn createdAt;
}
