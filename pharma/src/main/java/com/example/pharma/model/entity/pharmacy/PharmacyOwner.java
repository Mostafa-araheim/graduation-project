package com.example.pharma.model.entity.pharmacy;

import com.example.pharma.model.entity.core.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "pharmacy_owner")
public class PharmacyOwner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer ownerId;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
}
