package com.example.pharma.model.P2P;

import com.example.pharma.model.core.CreatedAtColumn;
import com.example.pharma.model.core.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "p2p_listing")
public class P2PListing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer listingId;

    @ManyToOne
    @JoinColumn(name = "seller_id")
    private User seller;

    private String medicineName;
    private Integer quantity;
    private LocalDate expiryDate;
    private Float price;
    private String imageUrl;
    private String status;
    @Embedded
    private CreatedAtColumn createdAt;}
