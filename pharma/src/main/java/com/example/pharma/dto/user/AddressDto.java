package com.example.pharma.dto.user;

import jakarta.validation.constraints.NotBlank;

public record AddressDto(
        Long addressId,
        @NotBlank(message = "Street is required")
        String street,
        @NotBlank(message = "City is required")
        String city,
        String postalCode,
        @NotBlank(message = "Country is required")
        String country,
        String apartmentNumber
) {}
