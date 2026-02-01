package com.example.pharma.model.entity.order;

import com.example.pharma.model.entity.core.CreatedAtColumn;
import com.example.pharma.model.entity.pharmacy.Pharmacy;
import com.example.pharma.model.entity.core.User;
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
    private Integer orderId;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private User customer;

    @ManyToOne
    @JoinColumn(name = "pharmacy_id")
    private Pharmacy pharmacy;

    private Float totalAmount;
    private String deliveryType;
    private String paymentMethod;
    private String status;
    @Embedded
    private CreatedAtColumn createdAt;}
