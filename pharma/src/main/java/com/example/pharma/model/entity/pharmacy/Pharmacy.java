package com.example.pharma.model.entity.pharmacy;

import com.example.pharma.model.entity.core.CreatedAtColumn;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "pharmacy")
public class Pharmacy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer pharmacyId;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private PharmacyOwner owner;

    private String name;
    private Float totalRating;
    private String imageUrl;
    @Embedded
    private CreatedAtColumn createdAt;}
