package com.example.pharma.security.handler;

import com.example.pharma.model.entity.core.User;
import com.example.pharma.repository.Core.UserRepository;
import com.example.pharma.security.jwt.JwtService;
import com.example.pharma.service.auth_services.TokenVerificationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtService jwtService;
    private final UserRepository userRepo;
    private final TokenVerificationService verificationTokenService;
    @Value("${spring.application.frontend.url}")
    private String frontendUrl;
    @Override
    public void onAuthenticationSuccess(@NonNull HttpServletRequest request,@NonNull HttpServletResponse response,@NonNull Authentication authentication) throws IOException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String code = verificationTokenService.generateVerificationToken(user);

        String redirectUrl = frontendUrl + "/auth/callback?code=" + code;
        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}
