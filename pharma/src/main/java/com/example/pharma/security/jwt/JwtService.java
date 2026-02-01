package com.example.pharma.security.jwt;

import com.example.pharma.model.core.User;
import com.example.pharma.repository.Core.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JwtService {
    private final Environment environment;
    private final UserRepository userRepository;

    public String generateToken(Authentication authentication) {
        String secret = environment.getProperty("JWT_SECRET", "d36d1a6448fbeb2b153814606126c5d39d12aa2be4c9a7153ff293baa6ac4b1a");
        SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        User user = userRepository.findByEmail(authentication.getName()).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return Jwts.builder()
                .subject(authentication.getName())
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
        String secret = environment.getProperty("JWT_SECRET", "d36d1a6448fbeb2b153814606126c5d39d12aa2be4c9a7153ff293baa6ac4b1a");
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }


    public Authentication parseAuthentication(String token) {
        Claims claims = extractAllClaims(token);
        String username = claims.getSubject();
        String authorities = claims.get("authorities", String.class);

        return new UsernamePasswordAuthenticationToken(
                username,
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
