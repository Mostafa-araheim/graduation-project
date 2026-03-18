package com.example.pharma.repository.Review;

import com.example.pharma.model.entity.review.PharmacyRating;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PharmacyRatingRepository extends JpaRepository<PharmacyRating, Long> {
}
