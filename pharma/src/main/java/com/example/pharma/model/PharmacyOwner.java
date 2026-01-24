package com.example.pharma.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "pharmacy_owner")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PharmacyOwner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "owner_id")
    private Long ownerId;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
}

