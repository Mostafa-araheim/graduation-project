package com.example.pharma.controller.pharmacy;

import com.example.pharma.dto.Product.ProductFilter;
import com.example.pharma.dto.Product.ProductResponse;
import com.example.pharma.dto.common.ApiResponse;
import com.example.pharma.dto.common.PageResponse;
import com.example.pharma.dto.order.response.OwnerOrderResponse;
import com.example.pharma.dto.pharmacy.PharmacyDto;
import com.example.pharma.dto.pharmacy.owner.*;
import com.example.pharma.dto.pharmacyProduct.AddPharmacyProductRequest;
import com.example.pharma.dto.pharmacyProduct.PharmacyProductDto;
import com.example.pharma.dto.pharmacyProduct.UpdatePharmacyProductRequest;
import com.example.pharma.security.AuthenticatedUser;
import com.example.pharma.service.pharmacy.PharmacyOwnerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/pharmacies")
@RequiredArgsConstructor
public class PharmacyOwnerController {

    private final PharmacyOwnerService pharmacyOwnerService;

    @PreAuthorize("hasRole('OWNER')")
    @PostMapping
    public ApiResponse<PharmacyDto> createPharmacy(
            @Valid @RequestBody CreatePharmacyRequest request,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser
    ) {
        return ApiResponse.success("Pharmacy created successfully",
                pharmacyOwnerService.createPharmacy(request, authenticatedUser.userId()));
    }

    @PreAuthorize("hasRole('OWNER')")
    @DeleteMapping("/{pharmacyId}")
    public ApiResponse<Void> deletePharmacy(
            @PathVariable Long pharmacyId,
            @AuthenticationPrincipal(expression = "userId") Long userId
    ) {
        pharmacyOwnerService.deletePharmacy(pharmacyId, userId);
        return ApiResponse.success("Pharmacy deleted successfully", null);
    }


    @PatchMapping("/{pharmacyId}")
    @PreAuthorize("hasRole('OWNER')")
    public ApiResponse<PharmacyDto> updatePharmacy(
            @PathVariable Long pharmacyId,
            @RequestBody UpdatePharmacyRequest request,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser
    ) {
        return ApiResponse.success("Pharmacy updated successfully",
                pharmacyOwnerService.updatePharmacy(pharmacyId, request, authenticatedUser.userId()));
    }

    @GetMapping("/owner")
    @PreAuthorize("hasRole('OWNER')")
    public ApiResponse<PageResponse<PharmacyDto>> getOwnerPharmacies(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @PageableDefault(size = 10, sort = "pharmacyId") Pageable pageable
    ) {
        return ApiResponse.success(
                "Owner pharmacies retrieved successfully",
                pharmacyOwnerService.getOwnerPharmacies(authenticatedUser.userId(), pageable)
        );
    }

    @GetMapping("/owner/{pharmacyId}")
    @PreAuthorize("hasRole('OWNER')")
    public ApiResponse<PharmacyDto> getOwnerPharmacyById(
            @PathVariable Long pharmacyId,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser
    ) {
        return ApiResponse.success("Pharmacy retrieved successfully",
                pharmacyOwnerService.getOwnerPharmacyById(pharmacyId, authenticatedUser.userId()));
    }


    @PostMapping("/{pharmacyId}/products")
    @PreAuthorize("hasRole('OWNER')")
    public ApiResponse<Void> addPharmacyProduct(
            @PathVariable Long pharmacyId,
            @Valid @RequestBody AddPharmacyProductRequest request,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser
    ) {
        pharmacyOwnerService.addPharmacyProduct(pharmacyId, request, authenticatedUser.userId());
        return ApiResponse.success("Product added to pharmacy inventory successfully", null);
    }

    @GetMapping("/{pharmacyId}/products")
    @PreAuthorize("hasRole('OWNER')")
    public ApiResponse<PageResponse<PharmacyProductDto>> getOwnerPharmacyProducts(
            @PathVariable Long pharmacyId,
            @ModelAttribute OwnerPharmacyProductFilter filter,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @PageableDefault(size = 10, sort = "pharmacyProductId") Pageable pageable
    ) {
        return ApiResponse.success(
                "Pharmacy products retrieved successfully",
                pharmacyOwnerService.getOwnerPharmacyProducts(
                        pharmacyId,
                        authenticatedUser.userId(),
                        filter,
                        pageable
                )
        );
    }


    @PatchMapping("/{pharmacyId}/products/{productId}")
    @PreAuthorize("hasRole('OWNER')")
    public ApiResponse<Void> updatePharmacyProduct(
            @PathVariable Long pharmacyId,
            @PathVariable Long productId,
            @Valid @RequestBody UpdatePharmacyProductRequest request,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser
    ) {
        pharmacyOwnerService.updatePharmacyProduct(pharmacyId, productId, request, authenticatedUser.userId());
        return ApiResponse.success("Pharmacy product updated successfully", null);
    }

    @DeleteMapping("/{pharmacyId}/products/{productId}")
    @PreAuthorize("hasRole('OWNER')")
    public ApiResponse<Void> deletePharmacyProduct(
            @PathVariable Long pharmacyId,
            @PathVariable Long productId,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser
    ) {
        pharmacyOwnerService.deletePharmacyProduct(pharmacyId, productId, authenticatedUser.userId());
        return ApiResponse.success("Pharmacy product deleted successfully", null);
    }

    @GetMapping("/{pharmacyId}/products/{productId}")
    @PreAuthorize("hasRole('OWNER')")
    public ApiResponse<PharmacyProductDto> getOwnerPharmacyProduct(
            @PathVariable Long pharmacyId,
            @PathVariable Long productId,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser
    ) {
        return ApiResponse.success(
                "Pharmacy product retrieved successfully",
                pharmacyOwnerService.getOwnerPharmacyProduct(pharmacyId, productId, authenticatedUser.userId())
        );
    }

    @GetMapping("/owner/dashboard-summary")
    @PreAuthorize("hasRole('OWNER')")
    public ApiResponse<OwnerDashboardSummaryResponse> getOwnerDashboardSummary(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser
    ) {
        return ApiResponse.success(
                "Owner dashboard summary retrieved successfully",
                pharmacyOwnerService.getOwnerDashboardSummary(authenticatedUser.userId())
        );
    }

    @GetMapping("/{pharmacyId}/dashboard-summary")
    @PreAuthorize("hasRole('OWNER')")
    public ApiResponse<PharmacyDashboardSummaryResponse> getPharmacyDashboardSummary(
            @PathVariable Long pharmacyId,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser
    ) {
        return ApiResponse.success(
                "Pharmacy dashboard summary retrieved successfully",
                pharmacyOwnerService.getPharmacyDashboardSummary(pharmacyId, authenticatedUser.userId())
        );
    }

    @GetMapping("/{pharmacyId}/orders")
    @PreAuthorize("hasRole('OWNER')")
    public ApiResponse<PageResponse<OwnerOrderResponse>> getOrdersForOwnerPharmacy(
            @PathVariable Long pharmacyId,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @PageableDefault(size = 10, sort = "orderId") Pageable pageable
    ) {
        return ApiResponse.success(
                "Pharmacy orders retrieved successfully",
                pharmacyOwnerService.getOrdersForOwnerPharmacy(
                        pharmacyId,
                        authenticatedUser.userId(),
                        pageable
                )
        );
    }

    @GetMapping("/owner/products/search")
    @PreAuthorize("hasRole('OWNER')")
    public ApiResponse<PageResponse<ProductResponse>> searchProductsToAdd(
            @ModelAttribute ProductFilter filter,
            @PageableDefault(size = 10, sort = "name") Pageable pageable
    ) {
        return ApiResponse.success(
                "Products retrieved successfully",
                pharmacyOwnerService.searchProductsToAdd(filter, pageable)
        );
    }

}
