package com.example.pharma.validators;

import com.example.pharma.dto.P2P.ListingRequest;
import com.example.pharma.exception.resource.EntityAlreadyExistsException;
import com.example.pharma.exception.validation.BusinessRuleViolationException;
import com.example.pharma.model.entity.P2P.ListingStatus;
import com.example.pharma.repository.P2P.P2PListingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class P2PListingValidator {

    private static final int MAX_ACTIVE_LISTINGS_PER_USER = 10;
    
    private final P2PListingRepository listingRepository;

    public void validateListingCreation(ListingRequest request) {
        if (listingRepository.existsBySeller_UserIdAndProduct_ProductIdAndExpiryDateAndStatus(
                request.sellerId(), request.productId(), request.expiryDate(), ListingStatus.AVAILABLE)) {
            throw new EntityAlreadyExistsException("You already have an active listing for this product with the same expiry date.");
        }

        if (listingRepository.countBySeller_UserIdAndStatus(request.sellerId(), ListingStatus.AVAILABLE) >= MAX_ACTIVE_LISTINGS_PER_USER) {
            throw new BusinessRuleViolationException("You have reached the maximum allowed active listings (" + MAX_ACTIVE_LISTINGS_PER_USER + ").");
        }
    }
}

