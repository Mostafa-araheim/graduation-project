package com.example.pharma.service;

import com.example.pharma.dto.P2P.ListingRequest;
import com.example.pharma.dto.P2P.ListingResponse;
import com.example.pharma.dto.events.ProductAvailableEvent;
import com.example.pharma.dto.P2P.UpdateListingRequest;
import com.example.pharma.dto.common.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.example.pharma.mapper.P2PListingMapper;
import com.example.pharma.model.entity.P2P.ListingStatus;
import com.example.pharma.model.entity.P2P.P2PListing;
import com.example.pharma.model.entity.catalog.Product;
import com.example.pharma.model.entity.core.CustomerProfile;
import com.example.pharma.repository.Catalog.ProductRepository;
import com.example.pharma.repository.Core.CustomerProfileRepository;
import com.example.pharma.repository.P2P.P2PListingRepository;
import com.example.pharma.service.interfaces.IP2PListingService;
import com.example.pharma.validators.P2PListingValidator;
import com.example.pharma.exception.resource.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import com.example.pharma.validators.SortValidator;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class P2PListingService implements IP2PListingService {

    private final P2PListingRepository listingRepository;
    private final CustomerProfileRepository customerProfileRepository;
    private final ProductRepository productRepository;
    private final P2PListingMapper listingMapper;
    private final P2PListingValidator p2pListingValidator;
    private final ApplicationEventPublisher eventPublisher;

    private static final Map<String, String> SORT_FIELD_MAPPING = Map.of(
            "price", "price",
            "quantity", "quantity",
            "productName", "productName",
            "expiryDate", "expiryDate",
            "listingId", "listingId"
    );

    @Override
    @Transactional
    public P2PListing createListing(ListingRequest request) {
        CustomerProfile seller = customerProfileRepository.findById(request.sellerId())
                .orElseThrow(() -> new EntityNotFoundException("Customer profile not found"));

        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));

        p2pListingValidator.validateListingCreation(request);

        P2PListing listing = listingMapper.toEntity(request, product, seller);

        listing.setStatus(ListingStatus.AVAILABLE);
        var saved = listingRepository.save(listing);
        // notify the waiting users
        eventPublisher.publishEvent(new ProductAvailableEvent(
                saved.getProduct().getProductId(),
                saved.getProductName(),
                "private user",
                "P2p"
                ));
        return saved;
    }

    @Override
    @Transactional
    public P2PListing updateListing(Long listingId, UpdateListingRequest request) {
        P2PListing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new EntityNotFoundException("Listing not found"));

        if (request.quantity() != null) {
            listing.setQuantity(request.quantity());
        }
        if (request.price() != null) {
            listing.setPrice(request.price());
        }
        if (request.additionalDetails() != null) {
            listing.setAdditionalDetails(request.additionalDetails());
        }
        if (request.imageUrl() != null) {
            listing.setImageUrl(request.imageUrl());
        }

        return listingRepository.save(listing);
    }

    @Override
    @Transactional
    public void deleteListing(Long listingId) {
        P2PListing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new EntityNotFoundException("Listing not found"));
        
        listingRepository.delete(listing);
    }

    @Override
    @Transactional(readOnly = true)
    public ListingResponse getListingById(Long listingId) {
        P2PListing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new EntityNotFoundException("Listing not found"));
        return listingMapper.toResponse(listing);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ListingResponse> getAllListings(Pageable pageable) {
        Sort mappedSort = SortValidator.validateAndMap(pageable.getSort(), SORT_FIELD_MAPPING);
        Pageable validatedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                mappedSort
        );
        Page<P2PListing> listings = listingRepository.findAll(validatedPageable);
        return PageResponse.from(listings.map(listingMapper::toResponse));
    }

    @Override
    @Transactional
    public int expireOldListings() {
        return listingRepository.expireOldListings(
                ListingStatus.EXPIRED,
                List.of(ListingStatus.AVAILABLE, ListingStatus.PENDING),
                java.time.LocalDate.now()
        );
    }
}
