package com.example.pharma.model.entity.core;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "pharmacist_profile")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PharmacistProfile {

    @Id
    @Column(name = "user_id")
    private Integer userId;

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @Embedded
    private CreatedAtColumn createdAt;
}