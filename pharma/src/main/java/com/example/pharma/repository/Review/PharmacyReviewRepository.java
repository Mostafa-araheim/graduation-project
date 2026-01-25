package com.example.pharma.repository.Review;

import com.example.pharma.model.review.PharmacyReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PharmacyReviewRepository extends JpaRepository<PharmacyReview, Integer> {
}
