package com.example.pharma.model.entity.review;

import com.example.pharma.model.entity.core.CreatedAtColumn;
import com.example.pharma.model.entity.core.CustomerProfile;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "seller_rating")
public class SellerRating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer ratingId;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private CustomerProfile seller;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id", nullable = false)
    private CustomerProfile buyer;

    private Integer rating;
    private String comment;

    @Embedded
    private CreatedAtColumn createdAt;
}