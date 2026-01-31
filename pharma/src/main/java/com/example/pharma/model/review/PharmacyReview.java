package com.example.pharma.model.review;

import com.example.pharma.model.core.CreatedAtColumn;
import com.example.pharma.model.pharmacy.Pharmacy;
import com.example.pharma.model.core.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "pharmacy_review")
public class PharmacyReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer reviewId;

    @ManyToOne
    @JoinColumn(name = "pharmacy_id")
    private Pharmacy pharmacy;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private User customer;

    private Integer rating;
    private String comment;
    @Embedded
    private CreatedAtColumn createdAt;}
