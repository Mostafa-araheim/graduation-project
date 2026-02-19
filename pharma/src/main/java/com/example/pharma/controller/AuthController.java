package com.example.pharma.controller;

import com.example.pharma.dto.EmailLoginRequest;
import com.example.pharma.dto.EmailSignUpRequest;
import com.example.pharma.model.entity.core.User;
import com.example.pharma.security.jwt.JwtService;
import com.example.pharma.service.auth_services.EmailAuthService;
import com.example.pharma.service.auth_services.TokenVerificationService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final EmailAuthService emailAuthService;
    private final TokenVerificationService verificationTokenService;
    private final JwtService jwtService;
    @PostMapping("/signup")
    public ResponseEntity<Void> signUpWithEmail(@Valid @RequestBody EmailSignUpRequest emailSignUpRequest)
    {
        emailAuthService.signup(emailSignUpRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    @PostMapping("/verify")
    public ResponseEntity<Void> verifyEmail(@RequestParam String token, HttpServletResponse response)
    {
        response.addHeader("jwt_token", emailAuthService.verifyEmail(token));
        return ResponseEntity.ok().build();
    }
    @PostMapping("/login")
    public ResponseEntity<Void> login(@Valid @RequestBody EmailLoginRequest request)
    {
        emailAuthService.login(request);
        return ResponseEntity.ok().build();
    }
    @PostMapping("/oauth/exchange")
    public ResponseEntity<Void> exchangeCode(@RequestParam String code, HttpServletResponse response)
    {
        User user = verificationTokenService.validateToken(code);
        Authentication auth = UsernamePasswordAuthenticationToken.authenticated(user.getEmail(), null, AuthorityUtils.commaSeparatedStringToAuthorityList(user.getRole().name()));
        String jwt = jwtService.generateToken(auth);
        response.addHeader("jwt_token", jwt);
        return ResponseEntity.ok().build();
    }
    @PostMapping("/resend-otp")
    public ResponseEntity<Void> resendOtp(@Valid @RequestBody EmailLoginRequest request) {
        emailAuthService.resendOtp(request);
        return ResponseEntity.ok().build();
    }
}
