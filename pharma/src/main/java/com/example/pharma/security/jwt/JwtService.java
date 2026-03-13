package com.example.pharma.security.jwt;

import com.example.pharma.exception.resource.EntityNotFoundException;
import com.example.pharma.model.entity.core.User;
import com.example.pharma.repository.Core.UserRepository;
import com.example.pharma.security.AuthenticatedUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;
import java.util.stream.Collectors;

;

@Service
@RequiredArgsConstructor
public class JwtService {
    private final Environment environment;
    private final UserRepository userRepository;

    public String generateToken(Authentication authentication) {
        String secret = environment.getProperty("spring.application.jwt.secret");
        SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        User user = userRepository.findByEmail(((AuthenticatedUser)authentication.getPrincipal()).email()).orElseThrow(() -> new EntityNotFoundException("User not found"));
        return Jwts.builder()
                .subject(((AuthenticatedUser) authentication.getPrincipal()).email())
                .claim("user_id", user.getUserId())
                .claim("authorities", authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(",")))
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000))
                .signWith(secretKey)
                .compact();
    }


    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    public Integer extractUserId(String token) {
        return Integer.parseInt(extractClaim(token, claims -> claims.get("user_id").toString()));
    }


    public SecretKey getSecretKey() {
        String secret = environment.getProperty("spring.application.jwt.secret");
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }


    public Authentication parseAuthentication(String token) {

        Claims claims = extractAllClaims(token);

        String email = claims.getSubject();
        Long userId = claims.get("user_id", Long.class);
        String authorities = claims.get("authorities", String.class);

        AuthenticatedUser principal =
                new AuthenticatedUser(userId, email);

        return new UsernamePasswordAuthenticationToken(
                principal,
                null,
                AuthorityUtils.commaSeparatedStringToAuthorityList(authorities)
        );
    }


    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
