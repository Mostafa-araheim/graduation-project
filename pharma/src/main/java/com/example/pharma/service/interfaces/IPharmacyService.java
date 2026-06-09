package com.example.pharma.service.interfaces;

import com.example.pharma.dto.common.PageResponse;
import com.example.pharma.dto.pharmacy.PharmacyDto;
import com.example.pharma.dto.pharmacy.PharmacyInfo;
import com.example.pharma.dto.pharmacy.PharmacySearchFilter;
import com.example.pharma.dto.pharmacy.owner.CreatePharmacyRequest;
import com.example.pharma.dto.pharmacyProduct.PharmacyProductDto;
import com.example.pharma.dto.review.CreateRatingDto;
import com.example.pharma.dto.review.CreateReviewDto;
import com.example.pharma.dto.review.ReviewDto;
import com.example.pharma.model.entity.review.PharmacyRating;
import com.example.pharma.security.AuthenticatedUser;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IPharmacyService {
     PageResponse<PharmacyDto> getPharmacies(PharmacySearchFilter pharmacySearchFilter,
                                                   Pageable pageable);
     PharmacyInfo getPharmacyInfo(Long pharmacyId);
     PageResponse<PharmacyProductDto> getPharmacyProductsUnderACategory(Long pharmacyId, Long categoryId, Pageable pageable);
    void createPharmacies(List<CreatePharmacyRequest> requests);

    PharmacyRating createRating(CreateRatingDto createRatingDto, AuthenticatedUser authenticatedUser);

    ReviewDto createPharmacyReview(CreateReviewDto createReviewDto, AuthenticatedUser authenticatedUser);

    List<PharmacyDto> getAllPharmacies();

}
