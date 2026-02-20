package com.example.pharma.service.auth_services;

import com.example.pharma.dto.EmailLoginRequest;
import com.example.pharma.dto.EmailSignUpRequest;
import com.example.pharma.model.auth.AuthProvider;
import com.example.pharma.model.auth.Provider;
import com.example.pharma.model.entity.core.User;
import com.example.pharma.model.entity.core.UserRole;
import com.example.pharma.repository.Auth.AuthProviderRepository;
import com.example.pharma.repository.Core.UserRepository;
import com.example.pharma.security.jwt.JwtService;
import com.example.pharma.service.EmailService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class EmailAuthService {
    private final UserRepository userRepo;
    private final AuthProviderRepository providerRepo;
    private final EmailService emailService;
    private final TokenVerificationService tokenVerificationService;
    private final JwtService jwtService;

    @Transactional
    public void signup(EmailSignUpRequest request) {

        validateSignUpRequest(request);

        User existingUser = userRepo.findByEmail(request.email()).orElse(null);

        if (existingUser != null ) {
            throw new IllegalStateException("Email already registered");
        }
        User user;
        if (existingUser != null) {
            user = existingUser;
            user.setName(request.userName());
        } else {
            user = new User();
            user.setName(request.userName());
            user.setEmail(request.email());
            user.setRoles(Set.of(UserRole.CUSTOMER));
            userRepo.save(user);
        }

        AuthProvider provider = new AuthProvider();
        provider.setUser(user);
        provider.setProvider(Provider.EMAIL);
        providerRepo.save(provider);

        String token = tokenVerificationService.generateVerificationToken(user);
        emailService.sendEmail(
                user.getEmail(),
                "Your verification code is: " + token,
                "Email Verification"
        );
    }

    @Transactional
    public void login(EmailLoginRequest request) {
        User user = userRepo.findByEmail(request.email()).orElseThrow(() -> new BadCredentialsException("Email Not found"));

        String token = tokenVerificationService.generateVerificationToken(user);
        emailService.sendEmail(
                user.getEmail(),
                "Your login code is: " + token,
                "Login Verification"
        );
    }

    private void validateSignUpRequest(EmailSignUpRequest request) {
        if (request.email() == null || !request.email().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Invalid email format");
        }
        if (request.userName() == null || request.userName().trim().isEmpty()) {
            throw new IllegalArgumentException("Username is required");
        }
    }

    @Transactional
    public String verifyEmail(String token) {
        User user = tokenVerificationService.validateToken(token);
        userRepo.save(user);
        Authentication auth = UsernamePasswordAuthenticationToken.authenticated(user.getEmail(), null, AuthorityUtils.commaSeparatedStringToAuthorityList(user.getRoles().stream()
                .map(r -> "ROLE_" + r.name())
                .toList().get(0)));
        return jwtService.generateToken(auth);
    }

    public void resendOtp(EmailLoginRequest request) {
        User user = userRepo.findByEmail(request.email())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        String token = tokenVerificationService.generateVerificationToken(user);
        emailService.sendEmail(
                user.getEmail(),
                "Your new code is: " + token,
                "Resend Verification"
        );
    }
}
