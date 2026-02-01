package com.example.pharma.repository.Review;

import com.example.pharma.model.entity.review.PharmacyReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PharmacyReviewRepository extends JpaRepository<PharmacyReview, Integer> {
}
