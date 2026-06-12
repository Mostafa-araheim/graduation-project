package com.example.pharma.service.pharmacy;

import com.example.pharma.dto.Product.ProductFilter;
import com.example.pharma.dto.Product.ProductResponse;
import com.example.pharma.dto.common.PageResponse;
import com.example.pharma.dto.events.ProductAvailableEvent;
import com.example.pharma.dto.order.response.OwnerOrderResponse;
import com.example.pharma.dto.pharmacy.PharmacyDto;
import com.example.pharma.dto.pharmacy.owner.*;
import com.example.pharma.dto.pharmacyProduct.AddPharmacyProductRequest;
import com.example.pharma.dto.pharmacyProduct.PharmacyProductDto;
import com.example.pharma.dto.pharmacyProduct.UpdatePharmacyProductRequest;
import com.example.pharma.dto.review.PharmacyReviewDetailDto;
import com.example.pharma.exception.access.AccessDeniedException;
import com.example.pharma.exception.resource.EntityNotFoundException;
import com.example.pharma.mapper.PharmacyProductMapper;
import com.example.pharma.mapper.ProductMapper;
import com.example.pharma.mapper.pharmacy.OwnerOrderMapper;
import com.example.pharma.mapper.pharmacy.PharmacyMapper;
import com.example.pharma.model.entity.catalog.Product;
import com.example.pharma.model.entity.core.OwnerProfile;
import com.example.pharma.model.entity.inventory.AvailabilityStatus;
import com.example.pharma.model.entity.inventory.Inventory;
import com.example.pharma.model.entity.inventory.PharmacyProduct;
import com.example.pharma.model.entity.order.OrderStatus;
import com.example.pharma.model.entity.pharmacy.Pharmacy;
import com.example.pharma.repository.Catalog.ProductRepository;
import com.example.pharma.repository.Core.OwnerProfileRepository;
import com.example.pharma.repository.Inventory.PharmacyProductRepository;
import com.example.pharma.repository.Order.OrderItemRepository;
import com.example.pharma.repository.Order.OrderRepository;
import com.example.pharma.repository.Pharmacy.PharmacyRepository;
import com.example.pharma.repository.Review.PharmacyRatingRepository;
import com.example.pharma.repository.Review.PharmacyReviewRepository;
import com.example.pharma.specification.OwnerPharmacyProductSpecification;
import com.example.pharma.specification.ProductSpecification;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

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
    private final OrderItemRepository orderItemRepository;
    private final OwnerOrderMapper ownerOrderMapper;
    private final ApplicationEventPublisher eventPublisher;
    private final ProductMapper productMapper;
    private final PharmacyReviewRepository pharmacyReviewRepository;
    private final PharmacyRatingRepository pharmacyRatingRepository;



    @Transactional
    public PharmacyDto createPharmacy(CreatePharmacyRequest request, Long ownerUserId) {

        OwnerProfile owner = ownerProfileRepository.findById(ownerUserId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Owner profile not found"));

        Pharmacy pharmacy = pharmacyMapper.toEntity(request);

        if (request.image() != null && !request.image().isEmpty()) {
            String imageUrl = saveImage(request.image());
            pharmacy.setImageUrl(imageUrl);
        }

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
            
            eventPublisher.publishEvent(new ProductAvailableEvent(
                product.getProductId(),
                product.getName(),
                pharmacy.getName(),
                "PHARMACY"
            ));
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

        eventPublisher.publishEvent(new ProductAvailableEvent(
            product.getProductId(),
            product.getName(),
            pharmacy.getName(),
            "PHARMACY"
        ));
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
            OwnerPharmacyProductFilter filter,
            Pageable pageable
    ) {
        validateOwnerPharmacyAccess(pharmacyId, ownerUserId);

        Specification<PharmacyProduct> spec =
                OwnerPharmacyProductSpecification.build(pharmacyId, filter);

        return PageResponse.from(
                pharmacyProductRepository.findAll(spec, pageable)
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
    public PageResponse<ProductResponse> searchProductsToAdd(
            ProductFilter filter,
            Pageable pageable
    ) {
        Specification<Product> spec = ProductSpecification.buildFromFilter(filter);

        return PageResponse.from(
                productRepository.findAll(spec, pageable)
                        .map(productMapper::toResponse)
        );
    }

    @Transactional
    public OwnerDashboardSummaryResponse getOwnerDashboardSummary(Long ownerUserId) {
        Long totalPharmacies = pharmacyRepository.countByOwner_UserId(ownerUserId);
        Long totalProducts = pharmacyProductRepository.countProductsByOwner(ownerUserId);
        Long outOfStockCount = pharmacyProductRepository.countProductsByOwnerAndStatus(ownerUserId, AvailabilityStatus.OutOfStock);
        Long limitedSupplyCount = pharmacyProductRepository.countProductsByOwnerAndStatus(ownerUserId, AvailabilityStatus.LimitedSupply);
        Long totalOrders = orderRepository.countOrdersByOwner(ownerUserId);
        java.math.BigDecimal totalRevenue = orderRepository.sumRevenueByOwnerAndStatus(ownerUserId, OrderStatus.PLACED);

        return new OwnerDashboardSummaryResponse(
                totalPharmacies,
                totalProducts,
                outOfStockCount,
                limitedSupplyCount,
                totalOrders,
                totalRevenue
        );
    }


    @Transactional
    public PharmacyDashboardSummaryResponse getPharmacyDashboardSummary(
            Long pharmacyId,
            Long ownerUserId
    ) {
        validateOwnerPharmacyAccess(pharmacyId, ownerUserId);

        Long totalProducts = pharmacyProductRepository.countByInventory_PharmacyId(pharmacyId);
        Long outOfStockCount = pharmacyProductRepository.countByInventory_PharmacyIdAndAvailabilityStatus(pharmacyId, AvailabilityStatus.OutOfStock);
        Long limitedSupplyCount = pharmacyProductRepository.countByInventory_PharmacyIdAndAvailabilityStatus(pharmacyId, AvailabilityStatus.LimitedSupply);
        Long totalOrders = orderRepository.countByPharmacy_PharmacyId(pharmacyId);
        Long pendingOrders = orderRepository.countByPharmacy_PharmacyIdAndStatus(pharmacyId, OrderStatus.PENDING_PAYMENT);
        java.math.BigDecimal totalRevenue = orderRepository.sumRevenueByPharmacyIdAndStatus(pharmacyId, OrderStatus.PLACED);
        Double avgRating = pharmacyRatingRepository.findAverageRatingByPharmacyId(pharmacyId).orElse(null);
        Long totalReviews = pharmacyReviewRepository.countByPharmacy_PharmacyId(pharmacyId);

        return new PharmacyDashboardSummaryResponse(
                pharmacyId,
                totalProducts,
                outOfStockCount,
                limitedSupplyCount,
                totalOrders,
                pendingOrders,
                totalRevenue,
                avgRating,
                totalReviews
        );
    }

    @Transactional
    public PageResponse<PharmacyReviewDetailDto> getPharmacyReviews(
            Long pharmacyId,
            Long ownerUserId,
            org.springframework.data.domain.Pageable pageable
    ) {
        validateOwnerPharmacyAccess(pharmacyId, ownerUserId);

        return PageResponse.from(
                pharmacyReviewRepository.findByPharmacy_PharmacyId(pharmacyId, pageable)
                        .map(review -> new PharmacyReviewDetailDto(
                                review.getReviewId(),
                                review.getCustomer().getUser().getName(),
                                review.getComment(),
                                review.getCreatedAt() != null ? review.getCreatedAt().getValue() : null
                        ))
        );
    }

    @Transactional
    public OwnerProfileDto getOwnerProfile(Long ownerUserId) {
        OwnerProfile owner = ownerProfileRepository.findById(ownerUserId)
                .orElseThrow(() -> new EntityNotFoundException("Owner profile not found"));

        Long totalPharmacies = pharmacyRepository.countByOwner_UserId(ownerUserId);

        return new OwnerProfileDto(
                owner.getUserId(),
                owner.getUser().getName(),
                owner.getUser().getEmail(),
                owner.getUser().getPhone(),
                owner.getUser().getImageUrl(),
                owner.getCreatedAt() != null ? owner.getCreatedAt().getValue() : null,
                totalPharmacies
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

    @Transactional
    public SalesAnalyticsResponse getPharmacySalesAnalytics(Long pharmacyId, Long ownerUserId, String period) {
        validateOwnerPharmacyAccess(pharmacyId, ownerUserId);
        LocalDateTime startDate = resolveStartDate(period);

        // Fetch revenue data
        List<OrderRevenueProjection> orders = orderRepository.findRevenueData(pharmacyId, startDate);

        return buildSalesAnalyticsResponse(orders, startDate, (statuses, limit) ->
            orderItemRepository.findBestSellers(pharmacyId, statuses, startDate, PageRequest.of(0, limit))
        );
    }

    @Transactional
    public SalesAnalyticsResponse getOwnerSalesAnalytics(Long ownerUserId, String period) {
        // Verify owner exists
        if (!ownerProfileRepository.existsById(ownerUserId)) {
            throw new EntityNotFoundException("Owner profile not found");
        }
        LocalDateTime startDate = resolveStartDate(period);

        // Fetch revenue data
        List<OrderRevenueProjection> orders = orderRepository.findRevenueDataForOwner(ownerUserId, startDate);

        return buildSalesAnalyticsResponse(orders, startDate, (statuses, limit) ->
            orderItemRepository.findBestSellersForOwner(ownerUserId, statuses, startDate, PageRequest.of(0, limit))
        );
    }

    private LocalDateTime resolveStartDate(String period) {
        if (period == null) {
            period = "month";
        }
        LocalDateTime now = LocalDateTime.now();
        switch (period.toLowerCase()) {
            case "week":
                return now.minusDays(7);
            case "year":
                return now.minusDays(365);
            case "month":
            default:
                return now.minusDays(30);
        }
    }

    @FunctionalInterface
    private interface BestSellersFetcher {
        List<ProductSalesProjection> fetch(List<OrderStatus> statuses, int limit);
    }

    private SalesAnalyticsResponse buildSalesAnalyticsResponse(
            List<OrderRevenueProjection> orders,
            LocalDateTime startDate,
            BestSellersFetcher bestSellersFetcher
    ) {
        List<OrderStatus> revenueStatuses = List.of(OrderStatus.PLACED, OrderStatus.CONFIRMED);

        // 1. Calculate General Metrics
        BigDecimal totalRevenue = orders.stream()
                .filter(o -> revenueStatuses.contains(o.getStatus()))
                .map(OrderRevenueProjection::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long totalOrders = orders.size();

        long revenueOrdersCount = orders.stream()
                .filter(o -> revenueStatuses.contains(o.getStatus()))
                .count();

        BigDecimal averageOrderValue = revenueOrdersCount > 0
                ? totalRevenue.divide(BigDecimal.valueOf(revenueOrdersCount), 2, java.math.RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        // 2. Sales Over Time
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd");
        Map<String, List<OrderRevenueProjection>> groupedByDate = orders.stream()
                .collect(Collectors.groupingBy(o -> o.getCreatedAtValue().format(formatter)));

        List<DailySalesDto> salesOverTime = groupedByDate.entrySet().stream()
                .map(entry -> {
                    String dateStr = entry.getKey();
                    List<OrderRevenueProjection> dayOrders = entry.getValue();
                    BigDecimal dayRevenue = dayOrders.stream()
                            .filter(o -> revenueStatuses.contains(o.getStatus()))
                            .map(OrderRevenueProjection::getTotalPrice)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    long dayOrderCount = dayOrders.size();
                    return new DailySalesDto(dateStr, dayRevenue, dayOrderCount);
                })
                .sorted(java.util.Comparator.comparing(DailySalesDto::date))
                .collect(Collectors.toList());

        // 3. Best Sellers
        List<ProductSalesProjection> bestSellerProjections = bestSellersFetcher.fetch(revenueStatuses, 5);
        List<BestSellerProductDto> bestSellers = bestSellerProjections.stream()
                .map(p -> new BestSellerProductDto(
                        p.getProductId(),
                        p.getProductName(),
                        p.getQuantitySold(),
                        p.getTotalRevenue()
                ))
                .collect(Collectors.toList());

        // 4. Status Distribution
        Map<String, Long> statusDistribution = orders.stream()
                .collect(Collectors.groupingBy(
                        o -> o.getStatus().name(),
                        Collectors.counting()
                ));

        return new SalesAnalyticsResponse(
                totalRevenue,
                totalOrders,
                averageOrderValue,
                salesOverTime,
                bestSellers,
                statusDistribution
        );
    }
    private String saveImage(MultipartFile image) {

        try {

            String uploadDir = System.getProperty("user.dir")
                    + "/images/pharmacies/";

            File directory = new File(uploadDir);

            if (!directory.exists()) {
                directory.mkdirs();
            }

            String originalFilename = image.getOriginalFilename();

            String fileName = UUID.randomUUID()
                    + "_" + originalFilename;

            Path filePath = Paths.get(uploadDir, fileName);

            Files.copy(
                    image.getInputStream(),
                    filePath,
                    StandardCopyOption.REPLACE_EXISTING
            );

            return "/images/pharmacies/" + fileName;

        } catch (IOException e) {
            throw new RuntimeException("Failed to save image", e);
        }
    }
}
