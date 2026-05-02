package com.example.pharma.controller.auth;

import com.example.pharma.dto.auth.AuthVerification;
import com.example.pharma.dto.auth.AuthVerificationResult;
import com.example.pharma.dto.auth.signup.SignupStartRequest;
import com.example.pharma.dto.auth.signup.SignupStartResponse;
import com.example.pharma.dto.auth.signup.SignupVerifyRequest;
import com.example.pharma.dto.common.ApiResponse;
import com.example.pharma.security.jwt.JwtService;
import com.example.pharma.service.auth.AuthSignupService;
import com.example.pharma.service.auth.RefreshTokenService;
import com.example.pharma.util.CookieUtils;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/auth/signup")
@RequiredArgsConstructor
public class AuthSignupController {

    private final AuthSignupService signupService;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/start")
    public ResponseEntity<ApiResponse<SignupStartResponse>> start(
            @Valid @RequestBody SignupStartRequest request) {

        SignupStartResponse response = signupService.start(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("", response));
    }

    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<AuthVerification>> verify(
            @Valid @RequestBody SignupVerifyRequest request,
            HttpServletResponse response) {

        AuthVerificationResult result = signupService.verify(request);

        response.setHeader("Authorization", "Bearer " + result.jwt());



        String refreshToken = refreshTokenService.createRefreshToken(result.user().userId()).getToken();
        CookieUtils.addRefreshTokenCookie(response, refreshToken);



        return ResponseEntity.ok(
                ApiResponse.success("User is verified successfully",result.user())
        );
    }
}
