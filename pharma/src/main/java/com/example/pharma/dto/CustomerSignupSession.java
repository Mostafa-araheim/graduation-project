package com.example.pharma.dto;

import java.io.Serializable;

public record CustomerSignupSession(
        String email,
        String name,
        String codeHash,
        int attempts
) implements Serializable {}