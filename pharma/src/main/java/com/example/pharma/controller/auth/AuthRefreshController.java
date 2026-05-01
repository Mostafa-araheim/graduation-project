package com.example.pharma.controller.auth;

import com.example.pharma.dto.common.ApiResponse;
import com.example.pharma.model.entity.core.User;
import com.example.pharma.model.entity.security.RefreshToken;
import com.example.pharma.security.AuthenticatedUser;
import com.example.pharma.security.jwt.JwtService;
import com.example.pharma.service.auth.RefreshTokenService;
import com.example.pharma.util.CookieUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthRefreshController {

    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<Void>> refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        String refreshToken = CookieUtils.extractRefreshTokenFromCookies(request);

        RefreshToken newRefreshToken =
                refreshTokenService.rotateRefreshToken(refreshToken);

        User user = newRefreshToken.getUser();

        AuthenticatedUser principal =
                new AuthenticatedUser(user.getUserId(), user.getEmail());

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


        String newAccessToken = jwtService.generateToken(auth);


        response.setHeader("Authorization",  newAccessToken);
//        response.setHeader("X-Refresh-Token", newRefreshToken.getToken());
        CookieUtils.addRefreshTokenCookie(response, newRefreshToken.getToken());
        return ResponseEntity.ok(
                ApiResponse.success("Token refreshed successfully", null)
        );
    }
}
