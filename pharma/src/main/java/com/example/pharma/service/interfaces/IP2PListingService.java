package com.example.pharma.service.interfaces;

import com.example.pharma.dto.P2P.ListingRequest;
import com.example.pharma.model.entity.P2P.P2PListing;

public interface IP2PListingService {
    P2PListing createListing(ListingRequest request);
    void deleteListing(Long listingId);
}

