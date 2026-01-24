package com.example.pharma.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "pharmacy")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pharmacy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pharmacy_id")
    private Long pharmacyId;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private PharmacyOwner owner;

    @Column(name = "name")
    private String name;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "total_rating")
    private Float totalRating;

    @Column(name = "image_url")
    private String imageUrl;

    @OneToOne(mappedBy = "pharmacy", cascade = CascadeType.ALL)
    private PharmacyAddress address;
}
