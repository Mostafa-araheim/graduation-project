package com.example.pharma.controller;

import com.example.pharma.dto.*;
import com.example.pharma.model.entity.core.User;
import com.example.pharma.model.entity.core.UserRole;
import com.example.pharma.security.jwt.JwtService;
import com.example.pharma.service.auth_services.CustomerLoginService;
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
    private final CustomerLoginService loginService;

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
                        user.getRoles()
                                .stream()
                                .map(r -> r.name())
                                .toArray(String[]::new)
                )
        );

        String jwt = jwtService.generateToken(auth);
        response.setHeader("Authorization", "Bearer " + jwt);

        return ResponseEntity.ok().build();
    }
    @PostMapping("/login/start")
    public ResponseEntity<CustomerLoginStartResponse> loginStart(
            @RequestBody CustomerLoginStartRequest request) {

        return ResponseEntity.status(HttpStatus.OK)
                .body(loginService.start(request));
    }

    @PostMapping("/login/verify")
    public ResponseEntity<Void> loginVerify(
            @RequestBody CustomerLoginVerifyRequest request,
            HttpServletResponse response) {

        User user = loginService.verify(request);

        Authentication auth =
                UsernamePasswordAuthenticationToken.authenticated(
                        user.getEmail(),
                        null,
                        AuthorityUtils.createAuthorityList(
                                UserRole.ROLE_CUSTOMER.name()
                        )
                );

        String jwt = jwtService.generateToken(auth);

        response.setHeader("Authorization", "Bearer " + jwt);

        return ResponseEntity.ok().build();
    }
}