package com.example.pharma.model.entity.core;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "owner_profile")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OwnerProfile {

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
