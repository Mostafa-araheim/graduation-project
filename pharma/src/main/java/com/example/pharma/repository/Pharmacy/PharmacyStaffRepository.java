package com.example.pharma.repository.Pharmacy;

import com.example.pharma.model.entity.pharmacy.PharmacyStaff;
import com.example.pharma.model.entity.pharmacy.PharmacyStaffId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PharmacyStaffRepository extends JpaRepository<PharmacyStaff, PharmacyStaffId> {
    List<PharmacyStaff> findByPharmacy_PharmacyId(Integer pharmacyId);
    List<PharmacyStaff> findByPharmacist_UserId(Integer userId);
}
