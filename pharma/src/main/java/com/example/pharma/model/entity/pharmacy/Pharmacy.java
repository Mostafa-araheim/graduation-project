package com.example.pharma.model.entity.pharmacy;

import com.example.pharma.model.entity.core.CreatedAtColumn;
import com.example.pharma.model.entity.core.OwnerProfile;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Entity
@Getter
@Setter
@Table(name = "pharmacy")
public class Pharmacy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pharmacy_id")
    private Integer pharmacyId;

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_user_id", nullable = true)
    private OwnerProfile owner;

    private String name;

    @Column(name = "total_rating")
    private Float totalRating;

    @Column(name = "image_url")
    private String imageUrl;
    @Column(name = "latitude", nullable = true)
    private Double latitude;

    @Column(name = "longitude", nullable = true)
    private Double longitude;
    @Embedded
    private CreatedAtColumn createdAt;

    @OneToOne(mappedBy = "pharmacy", cascade = CascadeType.ALL, orphanRemoval = true)
    private PharmacyAddress address;

    @Column(name = "opening_time")
    private LocalTime openingTime;

    @Column(name = "closing_time")
    private LocalTime closingTime;

    @Column(name = "is_24_hours")
    private Boolean is24Hours;
}