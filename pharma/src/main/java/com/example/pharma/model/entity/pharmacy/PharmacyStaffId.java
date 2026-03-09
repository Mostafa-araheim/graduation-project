package com.example.pharma.model.entity.pharmacy;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PharmacyStaffId implements Serializable {

    @Column(name = "pharmacy_id")
    private Long pharmacyId;

    @Column(name = "user_id")
    private Long userId;
}
