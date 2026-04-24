package com.example.pharma.model.entity.pharmacy;

import com.example.pharma.model.entity.core.PharmacistProfile;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "pharmacy_staff")
public class PharmacyStaff {

    @EmbeddedId
    private PharmacyStaffId id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @MapsId("pharmacyId")
    @JoinColumn(name = "pharmacy_id")
    private Pharmacy pharmacy;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @MapsId("pharmacistUserId")
    @JoinColumn(name = "user_id")
    private PharmacistProfile pharmacist;

    @Enumerated(EnumType.STRING)
    private StaffRole staffRole;

    @Column(name = "active", nullable = false)
    private Boolean active = true;

    @Column(name = "joined_at", insertable = false, updatable = false)
    private LocalDateTime joinedAt;
}