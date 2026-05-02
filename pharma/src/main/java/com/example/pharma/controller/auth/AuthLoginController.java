package com.example.pharma.controller.auth;

import com.example.pharma.dto.auth.AuthVerification;
import com.example.pharma.dto.auth.AuthVerificationResult;
import com.example.pharma.dto.auth.login.LoginStartRequest;
import com.example.pharma.dto.auth.login.LoginStartResponse;
import com.example.pharma.dto.auth.login.LoginVerifyRequest;
import com.example.pharma.dto.common.ApiResponse;
import com.example.pharma.service.auth.AuthLoginService;
import com.example.pharma.service.auth.RefreshTokenService;
import com.example.pharma.util.CookieUtils;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth/login")
@RequiredArgsConstructor
public class AuthLoginController {

    private final AuthLoginService loginService;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/start")
    public ResponseEntity<ApiResponse<LoginStartResponse>> start(
            @Valid @RequestBody LoginStartRequest request) {

        LoginStartResponse response = loginService.start(request);

        return ResponseEntity.ok(ApiResponse.success("",response));
    }

    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<AuthVerification>> verify(
            @Valid @RequestBody LoginVerifyRequest request,
            HttpServletResponse response) {

        AuthVerificationResult result = loginService.verify(request);

        response.setHeader("Authorization", "Bearer " + result.jwt());


        String refreshToken = refreshTokenService
                .createRefreshToken(result.user().userId())
                .getToken();


//        response.setHeader("X-Refresh-Token", refreshToken);
        CookieUtils.addRefreshTokenCookie(response, refreshToken);


        return ResponseEntity.ok(
                ApiResponse.success("User verified successfully",result.user())
        );
    }

}
