package com.example.pharma.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CustomerSignupStartRequest(
        @JsonProperty("email") @Email @NotBlank String email,
        @JsonProperty("name")  @NotBlank String name
) {}