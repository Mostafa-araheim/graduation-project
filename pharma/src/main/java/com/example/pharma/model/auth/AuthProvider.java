package com.example.pharma.model.auth;


import com.example.pharma.model.entity.core.User;
import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Table(name = "auth_provider", uniqueConstraints = @UniqueConstraint(columnNames = {"provider", "providerUserId"}))
@Data
public class AuthProvider {

    @Id
    @GeneratedValue
    private UUID id;

    @JoinColumn(name = "user_id")
    @ManyToOne(optional = false)
    private User user;

    @Enumerated(EnumType.STRING)
    private Provider provider;

    private String providerUserId;
}
