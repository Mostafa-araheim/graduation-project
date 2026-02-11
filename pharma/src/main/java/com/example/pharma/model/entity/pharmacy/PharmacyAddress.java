package com.example.pharma.model.entity.pharmacy;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "pharmacy_address")
public class PharmacyAddress {

    @Id
    @Column(name = "pharmacy_id")
    private Integer pharmacyId;

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "pharmacy_id")
    private Pharmacy pharmacy;

    private String street;
    private String city;
    private String postalCode;
    private String country;
    private String apartmentNumber;
}