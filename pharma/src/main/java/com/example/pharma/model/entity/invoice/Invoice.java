package com.example.pharma.model.entity.invoice;

import com.example.pharma.model.entity.core.CreatedAtColumn;
import com.example.pharma.model.entity.order.Order;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "invoice")
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer invoiceId;

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", unique = true, nullable = false)
    private Order order;

    private String invoiceUrl;

    @Embedded
    private CreatedAtColumn createdAt;
}