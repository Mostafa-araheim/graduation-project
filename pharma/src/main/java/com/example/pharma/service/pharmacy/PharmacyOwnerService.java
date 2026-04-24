package com.example.pharma.service.pharmacy;

import com.example.pharma.dto.common.PageResponse;
import com.example.pharma.dto.order.response.OwnerOrderResponse;
import com.example.pharma.dto.pharmacy.*;
import com.example.pharma.dto.pharmacyProduct.AddPharmacyProductRequest;
import com.example.pharma.dto.pharmacyProduct.PharmacyProductDto;
import com.example.pharma.dto.pharmacyProduct.UpdatePharmacyProductRequest;
import com.example.pharma.exception.access.AccessDeniedException;
import com.example.pharma.exception.resource.EntityNotFoundException;
import com.example.pharma.mapper.PharmacyProductMapper;
import com.example.pharma.mapper.pharmacy.OwnerOrderMapper;
import com.example.pharma.mapper.pharmacy.PharmacyMapper;
import com.example.pharma.model.entity.catalog.Product;
import com.example.pharma.model.entity.core.OwnerProfile;
import com.example.pharma.model.entity.inventory.AvailabilityStatus;
import com.example.pharma.model.entity.inventory.Inventory;
import com.example.pharma.model.entity.inventory.PharmacyProduct;
import com.example.pharma.model.entity.pharmacy.Pharmacy;
import com.example.pharma.repository.Catalog.ProductRepository;
import com.example.pharma.repository.Core.OwnerProfileRepository;
import com.example.pharma.repository.Inventory.PharmacyProductRepository;
import com.example.pharma.repository.Order.OrderRepository;
import com.example.pharma.repository.Pharmacy.PharmacyRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PharmacyOwnerService {

    private final PharmacyRepository pharmacyRepository;
    private final OwnerProfileRepository ownerProfileRepository;
    private final PharmacyMapper pharmacyMapper;
    private final ProductRepository productRepository;
    private final PharmacyProductRepository pharmacyProductRepository;
    private final PharmacyProductMapper pharmacyProductMapper;
    private final OrderRepository orderRepository;
    private final OwnerOrderMapper ownerOrderMapper;

    @Transactional
    public PharmacyDto createPharmacy(CreatePharmacyRequest request, Long ownerUserId) {
        OwnerProfile owner = ownerProfileRepository.findById(ownerUserId)
                .orElseThrow(() -> new EntityNotFoundException("Owner profile not found"));

        Pharmacy pharmacy = pharmacyMapper.toEntity(request);
        pharmacy.setOwner(owner);

        if (pharmacy.getAddress() != null) {
            pharmacy.getAddress().setPharmacy(pharmacy);
        }

        Inventory inventory = new Inventory();
        inventory.setPharmacy(pharmacy);
        pharmacy.setInventory(inventory);

        return pharmacyMapper.toDto(pharmacyRepository.save(pharmacy));
    }

    @Transactional
    public void deletePharmacy(Long pharmacyId, Long ownerUserId) {
        Pharmacy pharmacy = validateOwnerPharmacyAccess(pharmacyId, ownerUserId);

        pharmacyRepository.delete(pharmacy);
    }

    private Pharmacy validateOwnerPharmacyAccess(Long pharmacyId, Long ownerUserId) {
        Pharmacy pharmacy = pharmacyRepository.findById(pharmacyId)
                .orElseThrow(() -> new EntityNotFoundException("Pharmacy not found"));

        if (pharmacy.getOwner() == null || !pharmacy.getOwner().getUserId().equals(ownerUserId)) {
            throw new AccessDeniedException("You are not allowed to manage this pharmacy");
        }

        return pharmacy;
    }


    @Transactional
    public PharmacyDto updatePharmacy(Long pharmacyId, UpdatePharmacyRequest request, Long ownerUserId) {
        Pharmacy pharmacy = validateOwnerPharmacyAccess(pharmacyId, ownerUserId);

        if (request.name() != null) {
            pharmacy.setName(request.name());
        }
        if (request.imageUrl() != null) {
            pharmacy.setImageUrl(request.imageUrl());
        }
        if (request.openingTime() != null) {
            pharmacy.setOpeningTime(request.openingTime());
        }
        if (request.closingTime() != null) {
            pharmacy.setClosingTime(request.closingTime());
        }
        if (request.is24Hours() != null) {
            pharmacy.setIs24Hours(request.is24Hours());
        }
        if (request.latitude() != null) {
            pharmacy.setLatitude(request.latitude());
        }
        if (request.longitude() != null) {
            pharmacy.setLongitude(request.longitude());
        }

        if (pharmacy.getAddress() != null) {
            if (request.street() != null) {
                pharmacy.getAddress().setStreet(request.street());
            }
            if (request.city() != null) {
                pharmacy.getAddress().setCity(request.city());
            }
            if (request.postalCode() != null) {
                pharmacy.getAddress().setPostalCode(request.postalCode());
            }
            if (request.country() != null) {
                pharmacy.getAddress().setCountry(request.country());
            }
            if (request.apartmentNumber() != null) {
                pharmacy.getAddress().setApartmentNumber(request.apartmentNumber());
            }
        }

        return pharmacyMapper.toDto(pharmacyRepository.save(pharmacy));
    }

    @Transactional
    public PageResponse<PharmacyDto> getOwnerPharmacies(Long ownerUserId, Pageable pageable) {
        return PageResponse.from(
                pharmacyRepository.findByOwner_UserId(ownerUserId, pageable)
                        .map(pharmacyMapper::toDto)
        );
    }

    @Transactional
    public PharmacyDto getOwnerPharmacyById(Long pharmacyId, Long ownerUserId) {
        return pharmacyMapper.toDto(validateOwnerPharmacyAccess(pharmacyId, ownerUserId));
    }

    @Transactional
    public void addPharmacyProduct(
            Long pharmacyId,
            AddPharmacyProductRequest request,
            Long ownerUserId
    ) {
        Pharmacy pharmacy = validateOwnerPharmacyAccess(pharmacyId, ownerUserId);

        Inventory inventory = pharmacy.getInventory();

        if (inventory == null) {
            throw new EntityNotFoundException("Inventory not found for this pharmacy");
        }

        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));

        PharmacyProduct existingProduct = pharmacyProductRepository
                .findByInventory_PharmacyIdAndProduct_ProductId(
                        pharmacyId,
                        request.productId()
                )
                .orElse(null);

        if (existingProduct != null) {
            existingProduct.setQuantity(existingProduct.getQuantity() + request.quantity());
            existingProduct.setPrice(request.price());
            existingProduct.setAvailabilityStatus(resolveAvailabilityStatus(existingProduct.getQuantity()));
            return;
        }

        PharmacyProduct pharmacyProduct = new PharmacyProduct();
        pharmacyProduct.setPharmacy(pharmacy);
        pharmacyProduct.setInventory(inventory);
        pharmacyProduct.setProduct(product);
        pharmacyProduct.setPrice(request.price());
        pharmacyProduct.setQuantity(request.quantity());
        pharmacyProduct.setAvailabilityStatus(resolveAvailabilityStatus(request.quantity()));

        pharmacyProductRepository.save(pharmacyProduct);
    }

    private AvailabilityStatus resolveAvailabilityStatus(Long quantity) {
        if (quantity == null || quantity <= 0) {
            return AvailabilityStatus.OutOfStock;
        }

        if (quantity <= 5) {
            return AvailabilityStatus.LimitedSupply;
        }

        return AvailabilityStatus.Available;
    }

    @Transactional
    public void updatePharmacyProduct(
            Long pharmacyId,
            Long productId,
            UpdatePharmacyProductRequest request,
            Long ownerUserId
    ) {
        validateOwnerPharmacyAccess(pharmacyId, ownerUserId);

        PharmacyProduct pharmacyProduct = pharmacyProductRepository
                .findByInventory_PharmacyIdAndProduct_ProductId(pharmacyId, productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found in this pharmacy"));

        if (request.quantity() != null) {
            pharmacyProduct.setQuantity(request.quantity());
            pharmacyProduct.setAvailabilityStatus(resolveAvailabilityStatus(request.quantity()));
        }

        if (request.price() != null) {
            pharmacyProduct.setPrice(request.price());
        }
    }

    @Transactional
    public void deletePharmacyProduct(
            Long pharmacyId,
            Long productId,
            Long ownerUserId
    ) {
        validateOwnerPharmacyAccess(pharmacyId, ownerUserId);

        PharmacyProduct pharmacyProduct = pharmacyProductRepository
                .findByInventory_PharmacyIdAndProduct_ProductId(pharmacyId, productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found in this pharmacy"));

        pharmacyProductRepository.delete(pharmacyProduct);
    }

    @Transactional
    public PageResponse<PharmacyProductDto> getOwnerPharmacyProducts(
            Long pharmacyId,
            Long ownerUserId,
            Pageable pageable
    ) {
        Pharmacy pharmacy = validateOwnerPharmacyAccess(pharmacyId, ownerUserId);

        return PageResponse.from(
                pharmacyProductRepository.findByInventory(pharmacy.getInventory(), pageable)
                        .map(pharmacyProductMapper::toPharmacyProductDto)
        );
    }

    @Transactional
    public PharmacyProductDto getOwnerPharmacyProduct(
            Long pharmacyId,
            Long productId,
            Long ownerUserId
    ) {
        validateOwnerPharmacyAccess(pharmacyId, ownerUserId);

        PharmacyProduct pharmacyProduct = pharmacyProductRepository
                .findByInventory_PharmacyIdAndProduct_ProductId(pharmacyId, productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found in this pharmacy"));

        return pharmacyProductMapper.toPharmacyProductDto(pharmacyProduct);
    }


    @Transactional
    public OwnerDashboardSummaryResponse getOwnerDashboardSummary(Long ownerUserId) {
        Long totalPharmacies = pharmacyRepository.countByOwner_UserId(ownerUserId);

        Long totalProducts = pharmacyProductRepository.countProductsByOwner(ownerUserId);

        Long outOfStockCount = pharmacyProductRepository.countProductsByOwnerAndStatus(
                ownerUserId,
                AvailabilityStatus.OutOfStock
        );

        Long limitedSupplyCount = pharmacyProductRepository.countProductsByOwnerAndStatus(
                ownerUserId,
                AvailabilityStatus.LimitedSupply
        );

        return new OwnerDashboardSummaryResponse(
                totalPharmacies,
                totalProducts,
                outOfStockCount,
                limitedSupplyCount
        );
    }


    @Transactional
    public PharmacyDashboardSummaryResponse getPharmacyDashboardSummary(
            Long pharmacyId,
            Long ownerUserId
    ) {
        validateOwnerPharmacyAccess(pharmacyId, ownerUserId);

        Long totalProducts = pharmacyProductRepository.countByInventory_PharmacyId(pharmacyId);

        Long outOfStockCount = pharmacyProductRepository
                .countByInventory_PharmacyIdAndAvailabilityStatus(
                        pharmacyId,
                        AvailabilityStatus.OutOfStock
                );

        Long limitedSupplyCount = pharmacyProductRepository
                .countByInventory_PharmacyIdAndAvailabilityStatus(
                        pharmacyId,
                        AvailabilityStatus.LimitedSupply
                );

        return new PharmacyDashboardSummaryResponse(
                pharmacyId,
                totalProducts,
                outOfStockCount,
                limitedSupplyCount
        );
    }

    @Transactional
    public PageResponse<OwnerOrderResponse> getOrdersForOwnerPharmacy(
            Long pharmacyId,
            Long ownerUserId,
            Pageable pageable
    ) {
        validateOwnerPharmacyAccess(pharmacyId, ownerUserId);

        return PageResponse.from(
                orderRepository.findByPharmacy_PharmacyId(pharmacyId, pageable)
                        .map(ownerOrderMapper::toResponse)
        );
    }



}
