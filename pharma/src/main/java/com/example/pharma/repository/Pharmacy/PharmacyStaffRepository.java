package com.example.pharma.repository.Pharmacy;

import com.example.pharma.model.entity.pharmacy.PharmacyStaff;
import com.example.pharma.model.entity.pharmacy.PharmacyStaffId;
import com.example.pharma.model.entity.pharmacy.StaffRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface PharmacyStaffRepository extends JpaRepository<PharmacyStaff, PharmacyStaffId> {
    boolean existsByPharmacy_PharmacyIdAndPharmacist_UserIdAndActiveTrue(
            Long pharmacyId,
            Long pharmacistUserId
    );

    boolean existsByPharmacy_PharmacyIdAndPharmacist_UserIdAndActiveTrueAndStaffRoleIn(
            Long pharmacyId,
            Long pharmacistUserId,
            Collection<StaffRole> staffRoles
    );

    Optional<PharmacyStaff> findByPharmacy_PharmacyIdAndPharmacist_UserId(
            Long pharmacyId,
            Long pharmacistUserId
    );

    List<PharmacyStaff> findByPharmacy_PharmacyIdAndActiveTrue(Long pharmacyId);
}