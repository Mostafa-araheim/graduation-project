package com.example.pharma.model.entity.P2P;

import com.example.pharma.model.entity.core.CreatedAtColumn;
import com.example.pharma.model.entity.core.CustomerProfile;
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
    private Long listingId;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private CustomerProfile seller;

    private String medicineName;
    private Long quantity;
    private LocalDate expiryDate;
    private Float price;
    private String imageUrl;
    private String status;

    @Embedded
    private CreatedAtColumn createdAt;
}