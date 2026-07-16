package com.example.pharma.config;

import com.example.pharma.security.filter.JwtValidatorFilter;
import com.example.pharma.service.auth.RefreshTokenService;
import com.example.pharma.util.CookieUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtValidatorFilter jwtValidatorFilter;
    private final RefreshTokenService refreshTokenService;
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/api/**").permitAll()
                        .anyRequest().permitAll() // TODO: revert to .authenticated() after testing
                )
                .addFilterBefore(jwtValidatorFilter, UsernamePasswordAuthenticationFilter.class)
                .logout(logout -> logout
                .logoutUrl("/api/v1/auth/logout")
                .addLogoutHandler((request, response, authentication) -> {
                    String token = CookieUtils.extractRefreshTokenFromCookies(request);
                    if (token != null) {
                        refreshTokenService.revokeToken(token);
                    }
                })
                .logoutSuccessHandler((request, response, authentication) -> {
                    ResponseCookie deleteCookie = ResponseCookie.from("refresh_token", "")
                            .httpOnly(true)
                            .secure(true)
                            .path("/api/v1/auth")
                            .maxAge(0)
                            .sameSite("None")
                            .build();

                    response.setHeader(HttpHeaders.SET_COOKIE, deleteCookie.toString());
                })
                .permitAll()
        );

        return http.build();
    }
    @Bean
    CorsConfigurationSource corsConfigurationSource()
    {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:4200", "http://localhost:65503"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.addAllowedHeader("*");
        configuration.setAllowCredentials(true);
        configuration.addExposedHeader("Authorization");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
