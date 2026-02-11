package com.example.pharma.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record VerifySignupRequest(
        @JsonProperty("signupId") String signupId,
        @JsonProperty("code") String code
) {}