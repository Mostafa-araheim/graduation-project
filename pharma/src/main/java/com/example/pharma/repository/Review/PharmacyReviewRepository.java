package com.example.pharma.repository.Review;

import com.example.pharma.dto.review.ReviewDto;
import com.example.pharma.model.entity.review.PharmacyReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PharmacyReviewRepository extends JpaRepository<PharmacyReview, Long> {
    List<PharmacyReview> findByCustomer_UserId(Long userId);
    List<PharmacyReview> findByPharmacy_PharmacyId(Long pharmacyId);

    @Query("SELECT new com.example.pharma.dto.review.ReviewDto(u.name, pr.comment) " +
            "FROM PharmacyReview pr " +
            "JOIN pr.customer c " +
            "JOIN c.user u " +
            "WHERE pr.pharmacy.pharmacyId = :pharmacyId")
    List<ReviewDto> findReviewDtosByPharmacyId(@Param("pharmacyId") Long pharmacyId);

}