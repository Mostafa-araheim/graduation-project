package com.example.pharma.repository.Review;

import com.example.pharma.model.entity.review.SellerRating;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SellerRatingRepository extends JpaRepository<SellerRating, Long> {
    List<SellerRating> findBySeller_UserId(Long userId);
    List<SellerRating> findByBuyer_UserId(Long userId);
}