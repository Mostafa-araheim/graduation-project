package com.example.pharma.dto;

import java.io.Serializable;

public record OwnerSignupSession(
        String email,
        String name,
        String codeHash,
        int attempts
) implements Serializable {}