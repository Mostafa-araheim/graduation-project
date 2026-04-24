package com.example.pharma.repository.Pharmacy;

import com.example.pharma.model.entity.pharmacy.HiringRequestStatus;
import com.example.pharma.model.entity.pharmacy.PharmacyHiringRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PharmacyHiringRequestRepository extends JpaRepository<PharmacyHiringRequest, Long> {

    boolean existsByPharmacy_PharmacyIdAndPharmacist_UserIdAndStatus(
            Long pharmacyId,
            Long pharmacistUserId,
            HiringRequestStatus status
    );

    Optional<PharmacyHiringRequest> findByRequestIdAndPharmacist_UserId(
            Long requestId,
            Long pharmacistUserId
    );

    List<PharmacyHiringRequest> findByPharmacist_UserIdAndStatus(
            Long pharmacistUserId,
            HiringRequestStatus status
    );
}
