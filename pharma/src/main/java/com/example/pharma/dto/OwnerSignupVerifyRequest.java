package com.example.pharma.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public record OwnerSignupVerifyRequest(
        @JsonProperty("signupId") @NotBlank String signupId,
        @JsonProperty("code")     @NotBlank String code
) {}
