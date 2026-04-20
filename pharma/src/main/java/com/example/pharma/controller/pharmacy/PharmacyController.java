package com.example.pharma.controller.pharmacy;

import com.example.pharma.dto.common.ApiResponse;
import com.example.pharma.dto.common.PageResponse;
import com.example.pharma.dto.pharmacy.CreatePharmacyRequest;
import com.example.pharma.dto.pharmacy.PharmacyDto;
import com.example.pharma.dto.pharmacy.PharmacyInfo;
import com.example.pharma.dto.pharmacy.PharmacySearchFilter;
import com.example.pharma.dto.pharmacyProduct.PharmacyProductDto;
import com.example.pharma.dto.review.CreateRatingDto;
import com.example.pharma.dto.review.CreateReviewDto;
import com.example.pharma.dto.review.ReviewDto;
import com.example.pharma.model.entity.core.CustomerProfile;
import com.example.pharma.model.entity.review.PharmacyRating;
import com.example.pharma.service.PharmacyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/pharmacies")
@RequiredArgsConstructor
public class PharmacyController {
    private final PharmacyService pharmacyService;

    @GetMapping("/locations")
    public ApiResponse<List<PharmacyDto>> getAllPharmacies()
    {
        return ApiResponse.success("All pharmacies returned successfully",pharmacyService.getAllPharmacies());
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<PageResponse<PharmacyDto>> getPharmacies(@Valid @ModelAttribute PharmacySearchFilter pharmacySearchFilter,
                                                                @PageableDefault(size = 10) Pageable pageable) {
        return ApiResponse.success("Pharmacies retrieved successfully", pharmacyService
                .getPharmacies(
                        pharmacySearchFilter.name(),
                        pharmacySearchFilter.minRating(),
                        pharmacySearchFilter.isOpen(),
                        pharmacySearchFilter.latitude(),
                        pharmacySearchFilter.longitude(),
                        pageable
                )
        );
    }
    @GetMapping("/{pharmacyId}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<PharmacyInfo> getPharmacyInfo(@PathVariable Long pharmacyId)
    {
        return ApiResponse.success("Pharmacy info fetched successfully",pharmacyService.getPharmacyInfo(pharmacyId));
    }
    @GetMapping("/{pharmacyId}/{categoryId}")
    public ApiResponse<PageResponse<PharmacyProductDto>> getPharmacyProductsUnderACategory(@PathVariable Long pharmacyId, @PathVariable Long categoryId, @PageableDefault(sort = "pharmacyProductId") Pageable pageable)
    {
        return ApiResponse.success("Products returned successfully",pharmacyService.getPharmacyProductsUnderACategory(pharmacyId, categoryId, pageable));

    }
    @PostMapping("/rating")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<PharmacyRating> createRating(@RequestBody CreateRatingDto createRatingDto, @AuthenticationPrincipal CustomerProfile customerProfile)
    {
        return ApiResponse.success("Rating submitted successfully", pharmacyService.createRating(createRatingDto, customerProfile));
    }


    @PostMapping("/review")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ReviewDto> createReview(@RequestBody CreateReviewDto createReviewDto,@AuthenticationPrincipal CustomerProfile customerProfile)
    {
        return ApiResponse.success("Review submitted successfully",pharmacyService.createPharmacyReview(createReviewDto, customerProfile) );
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createBulk(@RequestBody List<CreatePharmacyRequest> createPharmacyRequestList) {
        pharmacyService.createPharmacies(createPharmacyRequestList);
    }


}
