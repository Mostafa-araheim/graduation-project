package com.example.pharma.service.auth;

import com.example.pharma.exception.resource.EntityNotFoundException;
import com.example.pharma.model.entity.core.User;
import com.example.pharma.model.entity.security.RefreshToken;
import com.example.pharma.repository.Core.UserRepository;
import com.example.pharma.repository.security.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final long REFRESH_TOKEN_DURATION_MS = 7 * 24 * 60 * 60 * 1000;

    public RefreshToken createRefreshToken(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(REFRESH_TOKEN_DURATION_MS))
                .revoked(false)
                .createdAt(Instant.now())
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken validateRefreshToken(String token) {

        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Refresh token not found"));

        if (refreshToken.isRevoked()) {
            throw new RuntimeException("Refresh token revoked");
        }

        if (refreshToken.getExpiryDate().isBefore(Instant.now())) {
            throw new RuntimeException("Refresh token expired");
        }

        return refreshToken;
    }
    public RefreshToken rotateRefreshToken(String oldToken) {

        RefreshToken existingToken = validateRefreshToken(oldToken);


        existingToken.setRevoked(true);
        refreshTokenRepository.save(existingToken);


        return createRefreshToken(existingToken.getUser().getUserId());
    }

    public void revokeToken(String token) {

        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Refresh token not found"));

        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);
    }
}
