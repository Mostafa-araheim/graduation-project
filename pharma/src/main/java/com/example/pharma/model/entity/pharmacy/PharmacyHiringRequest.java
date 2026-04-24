package com.example.pharma.model.entity.pharmacy;

import com.example.pharma.model.entity.core.OwnerProfile;
import com.example.pharma.model.entity.core.PharmacistProfile;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "pharmacy_hiring_request")
public class PharmacyHiringRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long requestId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pharmacy_id", nullable = false)
    private Pharmacy pharmacy;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_user_id", nullable = false)
    private OwnerProfile owner;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pharmacist_user_id", nullable = false)
    private PharmacistProfile pharmacist;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private HiringRequestStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StaffRole requestedRole;

    private String message;
}
