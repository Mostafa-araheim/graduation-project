package com.example.pharma.repository.Review;

import com.example.pharma.model.entity.review.PharmacyRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PharmacyRatingRepository extends JpaRepository<PharmacyRating, Long> {
    @Query("SELECT AVG(r.rating) FROM PharmacyRating r WHERE r.pharmacy.pharmacyId = :pharmacyId")
    Optional<Double> findAverageRatingByPharmacyId(@Param("pharmacyId") Long pharmacyId);

    Long countByPharmacy_PharmacyId(Long pharmacyId);
}
