package com.example.pharma.repository.Review;

import com.example.pharma.dto.review.ReviewDto;
import com.example.pharma.model.entity.review.PharmacyReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PharmacyReviewRepository extends JpaRepository<PharmacyReview, Integer> {
    List<PharmacyReview> findByCustomer_UserId(Integer userId);
    List<PharmacyReview> findByPharmacy_PharmacyId(Integer pharmacyId);

    @Query("SELECT new com.example.pharma.model.dto.ReviewDto(u.name, pr.rating, pr.comment) " +
            "FROM PharmacyReview pr " +
            "JOIN pr.customer c " +
            "JOIN c.user u " +
            "WHERE pr.pharmacy.pharmacyId = :pharmacyId")
    List<ReviewDto> findReviewDtosByPharmacyId(@Param("pharmacyId") Integer pharmacyId);
}