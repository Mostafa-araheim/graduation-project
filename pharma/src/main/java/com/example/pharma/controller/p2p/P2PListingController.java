package com.example.pharma.controller.p2p;

import com.example.pharma.dto.P2P.ListingRequest;
import com.example.pharma.model.entity.P2P.P2PListing;
import com.example.pharma.service.interfaces.IP2PListingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteListing(@PathVariable("id") Long listingId) {
        listingService.deleteListing(listingId);
        return ResponseEntity.noContent().build();
    }
}

