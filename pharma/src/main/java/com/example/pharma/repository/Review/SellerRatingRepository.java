package com.example.pharma.repository.Review;

import com.example.pharma.model.entity.review.SellerRating;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SellerRatingRepository extends JpaRepository<SellerRating, Integer> {
    List<SellerRating> findBySeller_UserId(Integer userId);
    List<SellerRating> findByBuyer_UserId(Integer userId);
}