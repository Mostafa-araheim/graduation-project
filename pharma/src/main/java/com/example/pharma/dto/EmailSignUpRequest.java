package com.example.pharma.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;

public record EmailSignUpRequest(
        @JsonProperty("username") String userName,
        @Email @JsonProperty("email") String email
) {}
