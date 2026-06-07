package com.example.pharma.service;

import com.example.pharma.dto.P2P.ListingFilter;
import com.example.pharma.dto.P2P.ListingRequest;
import com.example.pharma.dto.P2P.ListingResponse;
import com.example.pharma.dto.P2P.UpdateListingRequest;
import com.example.pharma.dto.common.PageResponse;
import com.example.pharma.dto.events.ProductAvailableEvent;
import com.example.pharma.exception.resource.EntityNotFoundException;
import com.example.pharma.mapper.P2PListingMapper;
import com.example.pharma.model.entity.P2P.ListingSpecifications;
import com.example.pharma.model.entity.P2P.ListingStatus;
import com.example.pharma.model.entity.P2P.P2PListing;
import com.example.pharma.model.entity.catalog.Product;
import com.example.pharma.model.entity.core.CustomerProfile;
import com.example.pharma.repository.Catalog.ProductRepository;
import com.example.pharma.repository.Core.CustomerProfileRepository;
import com.example.pharma.repository.P2P.P2PListingRepository;
import com.example.pharma.service.interfaces.IP2PListingService;
import com.example.pharma.validators.P2PListingValidator;
import com.example.pharma.validators.SortValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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

    @Override    @Transactional
    public P2PListing createListing(Long userId,ListingRequest request) {
        CustomerProfile seller = customerProfileRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Customer profile not found"));

        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));

        p2pListingValidator.validateListingCreation(userId,request);

        String imageUrl = saveImage(request.image());

        P2PListing listing = listingMapper.toEntity(request, product, seller,  imageUrl);

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
    public P2PListing updateListing(Long userId, Long listingId, UpdateListingRequest request) {
        P2PListing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new EntityNotFoundException("Listing not found"));

        p2pListingValidator.validateListingOwnership(userId, listing);

        if (request.quantity() != null) {
            listing.setQuantity(request.quantity());
        }
        if (request.price() != null) {
            listing.setPrice(request.price());
        }
        if (request.additionalDetails() != null) {
            listing.setDescription(request.additionalDetails());
        }
        if (request.imageUrl() != null) {
            listing.setImageUrl(request.imageUrl());
        }

        return listingRepository.save(listing);
    }

    @Override
    @Transactional
    public void deleteListing(Long userId, Long listingId) {
        P2PListing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new EntityNotFoundException("Listing not found"));
        
        p2pListingValidator.validateListingOwnership(userId, listing);
        
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
    public PageResponse<ListingResponse> getAllListings(ListingFilter filter, Pageable pageable) {
        Sort mappedSort = SortValidator.validateAndMap(pageable.getSort(), SORT_FIELD_MAPPING);
        Pageable validatedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                mappedSort
        );

        Specification<P2PListing> spec = ListingSpecifications.build(filter);
        Page<P2PListing> listings = listingRepository.findAll(spec, validatedPageable);

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
    private String saveImage(MultipartFile image) {

        try {

            String uploadDir = System.getProperty("user.dir")
                    + "/images/surplus_medicines/";

            File directory = new File(uploadDir);

            if (!directory.exists()) {
                directory.mkdirs();
            }

            String originalFilename = image.getOriginalFilename();

            String fileName =
                    UUID.randomUUID() + "_" + originalFilename;

            Path filePath = Paths.get(uploadDir, fileName);

            Files.copy(
                    image.getInputStream(),
                    filePath,
                    StandardCopyOption.REPLACE_EXISTING
            );

            return  "/images/surplus_medicines/" + fileName;

        } catch (IOException e) {
            throw new RuntimeException("Failed to save image", e);
        }
    }
}
