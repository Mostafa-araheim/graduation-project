package com.example.pharma.repository.Auth;

import com.example.pharma.model.auth.AuthProvider;
import com.example.pharma.model.auth.Provider;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AuthProviderRepository extends JpaRepository<@NonNull AuthProvider,@NonNull UUID> {
    Optional<AuthProvider> findByProviderAndProviderUserId(Provider provider, String providerUserId);
}
