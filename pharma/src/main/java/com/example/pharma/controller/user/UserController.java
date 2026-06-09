package com.example.pharma.controller.user;

import com.example.pharma.dto.common.ApiResponse;
import com.example.pharma.dto.user.AddressDto;
import com.example.pharma.dto.user.UpdateProfileDto;
import com.example.pharma.dto.user.UserProfileDto;
import com.example.pharma.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<UserProfileDto> getMyProfile(
            @AuthenticationPrincipal(expression = "userId") Long userId
    ) {
        UserProfileDto profile = userService.getUserProfile(userId);
        return ApiResponse.success("Profile retrieved successfully", profile);
    }

    @PutMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<UserProfileDto> updateMyProfile(
            @AuthenticationPrincipal(expression = "userId") Long userId,
            @Valid @RequestBody UpdateProfileDto dto
    ) {
        UserProfileDto profile = userService.updateUserProfile(userId, dto);
        return ApiResponse.success("Profile updated successfully", profile);
    }

    @PostMapping(value = "/me/picture", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<UserProfileDto> uploadProfilePicture(
            @AuthenticationPrincipal(expression = "userId") Long userId,
            @RequestParam("file") MultipartFile file
    ) {
        UserProfileDto profile = userService.uploadProfilePicture(userId, file);
        return ApiResponse.success("Profile picture uploaded successfully", profile);
    }

    @PostMapping("/me/addresses")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<AddressDto> addAddress(
            @AuthenticationPrincipal(expression = "userId") Long userId,
            @Valid @RequestBody AddressDto dto
    ) {
        AddressDto address = userService.addAddress(userId, dto);
        return ApiResponse.success("Address added successfully", address);
    }

    @GetMapping("/me/addresses")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<List<AddressDto>> getMyAddresses(
            @AuthenticationPrincipal(expression = "userId") Long userId
    ) {
        List<AddressDto> addresses = userService.getUserAddresses(userId);
        return ApiResponse.success("Addresses retrieved successfully", addresses);
    }

    @PutMapping("/me/addresses/{addressId}")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<AddressDto> updateAddress(
            @AuthenticationPrincipal(expression = "userId") Long userId,
            @PathVariable Long addressId,
            @Valid @RequestBody AddressDto dto
    ) {
        AddressDto address = userService.updateAddress(userId, addressId, dto);
        return ApiResponse.success("Address updated successfully", address);
    }

    @DeleteMapping("/me/addresses/{addressId}")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<Void> deleteAddress(
            @AuthenticationPrincipal(expression = "userId") Long userId,
            @PathVariable Long addressId
    ) {
        userService.deleteAddress(userId, addressId);
        return ApiResponse.success("Address deleted successfully", null);
    }
}
