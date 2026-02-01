package com.example.pharma.service.auth_services;

import com.example.pharma.model.auth.VerificationToken;
import com.example.pharma.model.core.User;
import com.example.pharma.repository.Auth.VerificationTokenRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TokenVerificationService {
    private final VerificationTokenRepository verificationTokenRepo;
    public String generateVerificationToken(User user) {
        SecureRandom secureRandom = new SecureRandom();
        StringBuilder tokenBuilder = new StringBuilder();

        for (int i = 0; i < 8; i++) {
            int digit = secureRandom.nextInt(10);
            tokenBuilder.append(digit);
        }

        VerificationToken verificationToken = VerificationToken.builder()
                .token(tokenBuilder.toString())
                .user(user)
                .expiryDate(LocalDateTime.now().plus(Duration.ofMinutes(5)))
                .build();
        verificationTokenRepo.save(verificationToken);
        return verificationToken.getToken();
    }
    public User validateToken(String token) {
        VerificationToken verificationToken = verificationTokenRepo
                .findByToken(token)
                .orElseThrow(() -> new EntityNotFoundException("Invalid token"));

        if (verificationToken.isExpired()) {
            verificationTokenRepo.delete(verificationToken);
            throw new IllegalStateException("Token expired");
        }
        verificationTokenRepo.delete(verificationToken);
        return verificationToken.getUser();
    }
}
