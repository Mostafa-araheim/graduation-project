package com.example.pharma.model.invoice;

import com.example.pharma.model.order.Order;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "invoice")
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer invoiceId;

    @OneToOne
    @JoinColumn(name = "order_id")
    private Order order;

    private String invoiceUrl;


    private LocalDateTime createdAt;
}
