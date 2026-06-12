package com.example.pharma.dto.pharmacy.owner;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalTime;

public record CreatePharmacyRequest(

        Integer ownerUserId,

        String name,

        @JsonFormat(pattern = "HH:mm:ss")
        LocalTime openingTime,

        @JsonFormat(pattern = "HH:mm:ss")
        LocalTime closingTime,

        Boolean is24Hours,

        String street,

        String city,

        String postalCode,

        String country,

        String apartmentNumber,

        Double latitude,

        Double longitude,

        MultipartFile image
) {
}