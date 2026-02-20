package com.example.pharma.controller;

import com.example.pharma.dto.EmailSignUpRequest;
import com.example.pharma.dto.SignupResponse;
import com.example.pharma.dto.VerifySignupRequest;
import com.example.pharma.model.entity.core.User;
import com.example.pharma.model.entity.core.UserRole;
import com.example.pharma.security.jwt.JwtService;
import com.example.pharma.service.SignupService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final SignupService signupService;
    private final JwtService jwtService;

    @PostMapping("/customer/signup")
    public ResponseEntity<SignupResponse> customerSignup(@RequestBody EmailSignUpRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(signupService.startSignup(request, UserRole.CUSTOMER));
    }

    @PostMapping("/owner/signup")
    public ResponseEntity<SignupResponse> ownerSignup(@RequestBody EmailSignUpRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(signupService.startSignup(request, UserRole.OWNER));
    }

    @PostMapping("/customer/verify")
    public ResponseEntity<Void> customerVerify(@RequestBody VerifySignupRequest request, HttpServletResponse response) {
        return verifyAndReturnJwt(request, response, UserRole.CUSTOMER);
    }

    @PostMapping("/owner/verify")
    public ResponseEntity<Void> ownerVerify(@RequestBody VerifySignupRequest request, HttpServletResponse response) {
        return verifyAndReturnJwt(request, response, UserRole.OWNER);
    }

    private ResponseEntity<Void> verifyAndReturnJwt(VerifySignupRequest request,
                                                    HttpServletResponse response,
                                                    UserRole expectedRole) {

        User user = signupService.confirmSignup(request, expectedRole);

        Authentication auth = UsernamePasswordAuthenticationToken.authenticated(
                user.getEmail(),
                null,
                AuthorityUtils.createAuthorityList(
                        user.getRoles().stream().map(r -> "ROLE_" + r.name()).toArray(String[]::new)
                )
        );

        response.addHeader("jwt_token", jwtService.generateToken(auth));
        return ResponseEntity.ok().build();
    }
}
