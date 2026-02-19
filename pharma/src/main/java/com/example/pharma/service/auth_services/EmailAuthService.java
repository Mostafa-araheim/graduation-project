package com.example.pharma.service.auth_services;

import com.example.pharma.dto.EmailLoginRequest;
import com.example.pharma.dto.EmailSignUpRequest;
import com.example.pharma.exception.auth_exception.EmailAlreadyRegisteredException;
import com.example.pharma.exception.auth_exception.InvalidLoginException;
import com.example.pharma.model.auth.AuthProvider;
import com.example.pharma.model.auth.Provider;
import com.example.pharma.model.entity.core.User;
import com.example.pharma.model.entity.core.UserRole;
import com.example.pharma.repository.Auth.AuthProviderRepository;
import com.example.pharma.repository.Core.UserRepository;
import com.example.pharma.security.jwt.JwtService;
import com.example.pharma.service.EmailService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Service;

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

        User existingUser = userRepo.findByEmail(request.getEmail()).orElse(null);

        if (existingUser != null && existingUser.isEmailVerified()) {
            throw new EmailAlreadyRegisteredException("Email already registered");
        }
        User user;
        if (existingUser != null) {
            user = existingUser;
            user.setName(request.getUserName());
        } else {
            user = new User();
            user.setName(request.getUserName());
            user.setEmail(request.getEmail());
            user.setRole(UserRole.CUSTOMER);
            user.setEmailVerified(false);
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
    public void login(EmailLoginRequest request)
    {
        User user = userRepo.findByEmail(request.email()).orElseThrow(() -> new InvalidLoginException("Invalid Credentials"));
        if(!user.isEmailVerified())
        {
            throw new InvalidLoginException("Invalid Credentials");
        }
        String token = tokenVerificationService.generateVerificationToken(user);
        emailService.sendEmail(
                user.getEmail(),
                "Your login code is: " + token,
                "Login Verification"
        );
    }
    @Transactional
    public String verifyEmail(String token)
    {
        User user = tokenVerificationService.validateToken(token);
        user.setEmailVerified(true);
        userRepo.save(user);
        Authentication auth = UsernamePasswordAuthenticationToken.authenticated(user.getEmail(),null,  AuthorityUtils.commaSeparatedStringToAuthorityList(user.getRole().name()));
        return jwtService.generateToken(auth);
    }

    public void resendOtp(EmailLoginRequest request) {
        User user = userRepo.findByEmail(request.email())
                .orElseThrow(() -> new InvalidLoginException("User not found"));

        String token = tokenVerificationService.generateVerificationToken(user);
        emailService.sendEmail(
                user.getEmail(),
                "Your new code is: " + token,
                "Resend Verification"
        );
    }
}
