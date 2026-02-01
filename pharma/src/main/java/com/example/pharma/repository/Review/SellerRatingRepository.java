package com.example.pharma.repository.Review;

import com.example.pharma.model.entity.review.SellerRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SellerRatingRepository extends JpaRepository<SellerRating, Integer> { }

