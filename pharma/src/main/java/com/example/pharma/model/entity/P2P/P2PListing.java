package com.example.pharma.model.entity.P2P;

import com.example.pharma.model.entity.catalog.Product;
import com.example.pharma.model.entity.catalog.ProductCondition;
import com.example.pharma.model.entity.core.CreatedAtColumn;
import com.example.pharma.model.entity.core.CustomerProfile;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @JsonIgnore
    private CustomerProfile seller;

    private String productName;
    private Long quantity;
    private LocalDate expiryDate;
    private String description;
    private Float price;
    private String imageUrl;
    @Enumerated(EnumType.STRING)
    private ListingStatus status = ListingStatus.AVAILABLE;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "medicine_id", nullable = false)
    @JsonIgnore
    private Product product;
    @Enumerated(EnumType.STRING)
    private ProductCondition condition;
    String city;
    @Embedded
    private CreatedAtColumn createdAt ;
}