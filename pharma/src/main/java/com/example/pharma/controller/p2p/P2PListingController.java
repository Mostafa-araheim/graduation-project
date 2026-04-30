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
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/p2p/listings")
@RequiredArgsConstructor
public class P2PListingController {

    private final IP2PListingService listingService;

    @PostMapping
    public ResponseEntity<P2PListing> createListing(@Valid @RequestBody ListingRequest request) {
        P2PListing createdListing = listingService.createListing(request);
        return new ResponseEntity<>(createdListing, HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<P2PListing> updateListing(
            @PathVariable("id") Long listingId,
            @Valid @RequestBody UpdateListingRequest request) {
        P2PListing updatedListing = listingService.updateListing(listingId, request);
        return ResponseEntity.ok(updatedListing);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteListing(@PathVariable("id") Long listingId) {
        listingService.deleteListing(listingId);
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
