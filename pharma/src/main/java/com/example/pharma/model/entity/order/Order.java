package com.example.pharma.model.entity.order;

import com.example.pharma.model.entity.core.CreatedAtColumn;
import com.example.pharma.model.entity.core.CustomerProfile;
import com.example.pharma.model.entity.pharmacy.Pharmacy;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long orderId;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private CustomerProfile customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pharmacy_id")
    private Pharmacy pharmacy;

    private BigDecimal totalPrice;

    @Enumerated(EnumType.STRING)
    private DeliveryType deliveryType;


    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Column(name = "source_cart_id", nullable = false, unique = true)
    private Long sourceCartId;

    @Embedded
    private CreatedAtColumn createdAt;
}