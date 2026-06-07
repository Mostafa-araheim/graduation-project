package com.example.pharma.service.interfaces;

import com.example.pharma.dto.P2P.ListingFilter;
import com.example.pharma.dto.P2P.ListingRequest;
import com.example.pharma.dto.P2P.ListingResponse;
import com.example.pharma.dto.P2P.UpdateListingRequest;
import com.example.pharma.dto.common.PageResponse;
import com.example.pharma.model.entity.P2P.P2PListing;
import org.springframework.data.domain.Pageable;

public interface IP2PListingService {
    P2PListing createListing(Long userId,ListingRequest request);
    P2PListing updateListing(Long userId, Long listingId, UpdateListingRequest request);
    void deleteListing(Long userId, Long listingId);
    ListingResponse getListingById(Long listingId);
    PageResponse<ListingResponse> getAllListings(ListingFilter listingFilter, Pageable pageable);
    
    int expireOldListings();
}
