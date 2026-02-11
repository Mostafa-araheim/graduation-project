package com.example.pharma.controller;

import com.example.pharma.dto.CustomerSignupStartRequest;
import com.example.pharma.dto.CustomerSignupStartResponse;
import com.example.pharma.dto.CustomerSignupVerifyRequest;
import com.example.pharma.model.entity.core.User;
import com.example.pharma.security.jwt.JwtService;
import com.example.pharma.service.auth_services.CustomerSignupService;
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
@RequestMapping("/auth/customer")
@RequiredArgsConstructor
public class CustomerAuthController {

    private final CustomerSignupService signupService;
    private final JwtService jwtService;

    @PostMapping("/signup/start")
    public ResponseEntity<CustomerSignupStartResponse> start(@RequestBody CustomerSignupStartRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(signupService.start(request));
    }

    @PostMapping("/signup/verify")
    public ResponseEntity<Void> verify(@RequestBody CustomerSignupVerifyRequest request,
                                       HttpServletResponse response) {

        User user = signupService.verify(request);

        Authentication auth = UsernamePasswordAuthenticationToken.authenticated(
                user.getEmail(),
                null,
                AuthorityUtils.createAuthorityList(
                        user.getRoles().stream().map(r -> "ROLE_" + r.name()).toArray(String[]::new)
                )
        );

        String jwt = jwtService.generateToken(auth);
        response.setHeader("Authorization", "Bearer " + jwt);

        return ResponseEntity.ok().build();
    }
}