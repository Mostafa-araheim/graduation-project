package com.example.pharma.controller.p2p;

import com.example.pharma.dto.P2P.ListingRequest;
import com.example.pharma.dto.P2P.ListingResponse;
import com.example.pharma.dto.P2P.UpdateListingRequest;
import com.example.pharma.dto.common.PageResponse;
import com.example.pharma.model.entity.P2P.P2PListing;
import com.example.pharma.service.interfaces.IP2PListingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/p2p/listings")
@RequiredArgsConstructor
public class P2PListingController {

    private final IP2PListingService listingService;

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<P2PListing> createListing(@Valid @RequestBody ListingRequest request,
                                                    @AuthenticationPrincipal(expression = "userId") Long userId ) {
        P2PListing createdListing = listingService.createListing(userId,request);
        return new ResponseEntity<>(createdListing, HttpStatus.CREATED);
    }
    
    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<P2PListing> updateListing(
            @PathVariable("id") Long listingId,
            @AuthenticationPrincipal(expression = "userId") Long userId,
            @Valid @RequestBody UpdateListingRequest request) {
        P2PListing updatedListing = listingService.updateListing(userId, listingId, request);
        return ResponseEntity.ok(updatedListing);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Void> deleteListing(@PathVariable("id") Long listingId,
                                              @AuthenticationPrincipal(expression = "userId") Long userId) {
        listingService.deleteListing(userId, listingId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ListingResponse> getListingById(@PathVariable("id") Long listingId) {
        ListingResponse response = listingService.getListingById(listingId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<PageResponse<ListingResponse>> getAllListings(
            @PageableDefault(size = 10, sort = "listingId") Pageable pageable) {
        PageResponse<ListingResponse> response = listingService.getAllListings(pageable);
        return ResponseEntity.ok(response);
    }
}
