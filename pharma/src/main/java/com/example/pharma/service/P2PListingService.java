package com.example.pharma.service;

import com.example.pharma.dto.P2P.ListingRequest;
import com.example.pharma.dto.events.ProductAvailableEvent;
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

@Service
@RequiredArgsConstructor
public class P2PListingService implements IP2PListingService {

    private final P2PListingRepository listingRepository;
    private final CustomerProfileRepository customerProfileRepository;
    private final ProductRepository productRepository;
    private final P2PListingMapper listingMapper;
    private final P2PListingValidator p2pListingValidator;
    private final ApplicationEventPublisher eventPublisher;

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
    public void deleteListing(Long listingId) {
        P2PListing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new EntityNotFoundException("Listing not found"));
        
        listingRepository.delete(listing);
    }
}
