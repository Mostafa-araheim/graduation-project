package com.example.pharma.dto.pharmacy.owner;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalTime;

public record UpdatePharmacyRequest(
        @JsonProperty("name")
        String name,

        @JsonProperty("image_url")
        String imageUrl,

        @JsonFormat(pattern = "HH:mm:ss")
        LocalTime openingTime,

        @JsonFormat(pattern = "HH:mm:ss")
        LocalTime closingTime,

        @JsonProperty("is_24_hours")
        Boolean is24Hours,

        @JsonProperty("street")
        String street,

        @JsonProperty("city")
        String city,

        @JsonProperty("postal_code")
        String postalCode,

        @JsonProperty("country")
        String country,

        @JsonProperty("apartment_number")
        String apartmentNumber,

        @JsonProperty("latitude")
        Double latitude,

        @JsonProperty("longitude")
        Double longitude
) {
}
