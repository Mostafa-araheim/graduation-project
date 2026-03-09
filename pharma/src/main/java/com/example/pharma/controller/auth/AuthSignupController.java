package com.example.pharma.controller.auth;

import com.example.pharma.dto.auth.signup.SignupStartRequest;
import com.example.pharma.dto.auth.signup.SignupStartResponse;
import com.example.pharma.dto.auth.signup.SignupVerifyRequest;
import com.example.pharma.dto.common.ApiResponse;
import com.example.pharma.model.entity.core.User;
import com.example.pharma.security.jwt.JwtService;
import com.example.pharma.service.auth.AuthSignupService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
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
@RequestMapping("/auth/signup")
@RequiredArgsConstructor
public class AuthSignupController {

    private final AuthSignupService signupService;
    private final JwtService jwtService;

    @PostMapping("/start")
    public ResponseEntity<ApiResponse<SignupStartResponse>> start(
            @Valid @RequestBody SignupStartRequest request) {

        SignupStartResponse response = signupService.start(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("",response));
    }

    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<Void>> verify(
            @Valid @RequestBody SignupVerifyRequest request,
            HttpServletResponse response) {

        User user = signupService.verify(request);

        Authentication auth =
                UsernamePasswordAuthenticationToken.authenticated(
                        user.getEmail(),
                        null,
                        AuthorityUtils.createAuthorityList(
                                user.getRoles()
                                        .stream()
                                        .map(Enum::name)
                                        .toArray(String[]::new)
                        )
                );

        String jwt = jwtService.generateToken(auth);

        response.setHeader("Authorization", "Bearer " + jwt);

        return ResponseEntity.ok().build();
    }
}
