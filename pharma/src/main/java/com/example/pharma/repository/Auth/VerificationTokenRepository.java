package com.example.pharma.repository.Auth;

import com.example.pharma.model.auth.VerificationToken;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface VerificationTokenRepository extends JpaRepository<@NonNull VerificationToken,@NonNull UUID> {
    Optional<VerificationToken> findByToken(String token);
}
