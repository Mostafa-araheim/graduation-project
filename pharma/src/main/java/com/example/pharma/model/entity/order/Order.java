package com.example.pharma.model.entity.order;

import com.example.pharma.model.entity.core.CreatedAtColumn;
import com.example.pharma.model.entity.core.CustomerProfile;
import com.example.pharma.model.entity.pharmacy.Pharmacy;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Integer orderId;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private CustomerProfile customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pharmacy_id")
    private Pharmacy pharmacy;

    private Float totalAmount;
    private String deliveryType;
    private String paymentMethod;
    private String status;

    @Embedded
    private CreatedAtColumn createdAt;
}