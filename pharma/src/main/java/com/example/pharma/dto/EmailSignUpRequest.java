package com.example.pharma.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmailSignUpRequest {
    @JsonProperty("username")
    private String userName;
    @Email
    @JsonProperty("email")
    private String email;
}
