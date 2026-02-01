package com.example.pharma.model.entity.P2P;

import com.example.pharma.model.entity.core.CreatedAtColumn;
import com.example.pharma.model.entity.core.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

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
