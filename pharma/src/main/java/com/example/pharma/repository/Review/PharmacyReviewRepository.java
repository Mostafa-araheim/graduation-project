package com.example.pharma.repository.Review;

import com.example.pharma.model.entity.review.PharmacyReview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PharmacyReviewRepository extends JpaRepository<PharmacyReview, Integer> {
    List<PharmacyReview> findByCustomer_UserId(Integer userId);
    List<PharmacyReview> findByPharmacy_PharmacyId(Integer pharmacyId);
}