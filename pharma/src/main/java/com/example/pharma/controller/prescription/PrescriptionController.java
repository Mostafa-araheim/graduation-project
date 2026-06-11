package com.example.pharma.controller.prescription;

import com.example.pharma.dto.ai.NearbyPharmacyResponse;
import com.example.pharma.dto.cart.response.CartResponse;
import com.example.pharma.dto.common.ApiResponse;
import com.example.pharma.service.AIService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequestMapping("/api/v1/scan-prescription")
@RestController
@RequiredArgsConstructor
public class PrescriptionController {

    private final AIService aiService;


    @PostMapping("/scan")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ApiResponse<List<CartResponse>> scanPrescription(
            @RequestParam("file") MultipartFile file,
            @RequestParam Double userLatitude,
            @RequestParam Double userLongitude,
            @AuthenticationPrincipal(expression = "userId") Long userId
    ) {
        List<CartResponse> updatedCarts =
                aiService.scanPrescription(file, userLatitude, userLongitude, userId);

        return ApiResponse.success("Prescription scanned and medicines added to cart successfully", updatedCarts);
    }

    @PostMapping("/scan/nearby")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ApiResponse<List<NearbyPharmacyResponse>> scanPrescriptionNearby(
            @RequestParam("file") MultipartFile file,
            @RequestParam Double userLatitude,
            @RequestParam Double userLongitude
    ) {
        List<NearbyPharmacyResponse> nearbyPharmacies =
                aiService.scanPrescriptionNearby(file, userLatitude, userLongitude);

        return ApiResponse.success("Nearby pharmacies retrieved successfully", nearbyPharmacies);
    }
}

