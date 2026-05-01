package com.example.pharma.controller.auth;

import com.example.pharma.dto.auth.login.LoginStartRequest;
import com.example.pharma.dto.auth.login.LoginStartResponse;
import com.example.pharma.dto.auth.login.LoginVerifyRequest;
import com.example.pharma.dto.common.ApiResponse;
import com.example.pharma.model.entity.core.User;
import com.example.pharma.security.AuthenticatedUser;
import com.example.pharma.security.jwt.JwtService;
import com.example.pharma.service.auth.AuthLoginService;
import com.example.pharma.service.auth.RefreshTokenService;
import com.example.pharma.util.CookieUtils;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth/login")
@RequiredArgsConstructor
public class AuthLoginController {

    private final AuthLoginService loginService;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/start")
    public ResponseEntity<ApiResponse<LoginStartResponse>> start(
            @Valid @RequestBody LoginStartRequest request) {

        LoginStartResponse response = loginService.start(request);

        return ResponseEntity.ok(ApiResponse.success("",response));
    }

    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<Void>> verify(
            @Valid @RequestBody LoginVerifyRequest request,
            HttpServletResponse response) {

        User user = loginService.verify(request);

        AuthenticatedUser principal =
                new AuthenticatedUser(
                        user.getUserId(),
                        user.getEmail()
                );

        Authentication auth =
                new UsernamePasswordAuthenticationToken(
                        principal,
                        null,
                        AuthorityUtils.createAuthorityList(
                                user.getRoles()
                                        .stream()
                                        .map(Enum::name)
                                        .toArray(String[]::new)
                        )
                );


        String jwt = jwtService.generateToken(auth);
        String refreshToken = refreshTokenService
                .createRefreshToken(user.getUserId())
                .getToken();


        response.setHeader("Authorization", jwt);
//        response.setHeader("X-Refresh-Token", refreshToken);
        CookieUtils.addRefreshTokenCookie(response, refreshToken);

        return ResponseEntity.ok().build();
    }

}
