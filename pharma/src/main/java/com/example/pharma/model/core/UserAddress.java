package com.example.pharma.model.core;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "user_address")
public class UserAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userAddressId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String street;
    private String city;
    private String postalCode;
    private String country;
    private String apartmentNumber;
}

