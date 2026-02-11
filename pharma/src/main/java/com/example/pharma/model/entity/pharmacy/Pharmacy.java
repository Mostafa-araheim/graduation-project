package com.example.pharma.model.entity.pharmacy;

import com.example.pharma.model.entity.core.CreatedAtColumn;
import com.example.pharma.model.entity.core.OwnerProfile;
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
    @Column(name = "pharmacy_id")
    private Integer pharmacyId;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_user_id", nullable = false)
    private OwnerProfile owner;

    private String name;

    @Column(name = "total_rating")
    private Float totalRating;

    @Column(name = "image_url")
    private String imageUrl;

    @Embedded
    private CreatedAtColumn createdAt;
}