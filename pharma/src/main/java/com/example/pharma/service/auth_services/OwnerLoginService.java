package com.example.pharma.service.auth_services;

import com.example.pharma.dto.OwnerLoginSession;
import com.example.pharma.dto.OwnerLoginStartRequest;
import com.example.pharma.dto.OwnerLoginStartResponse;
import com.example.pharma.dto.OwnerLoginVerifyRequest;
import com.example.pharma.model.entity.core.User;
import com.example.pharma.model.entity.core.UserRole;
import com.example.pharma.repository.Core.UserRepository;
import com.example.pharma.service.EmailService;
import com.example.pharma.util.RedisKeys;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.HexFormat;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OwnerLoginService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final UserRepository userRepo;
    private final EmailService emailService;

    private static final Duration TTL = Duration.ofMinutes(3);
    private static final int MAX_ATTEMPTS = 3;

    public OwnerLoginStartResponse start(OwnerLoginStartRequest req) {

        User user = userRepo
                .findByEmailAndRolesContaining(req.email(), UserRole.ROLE_OWNER)
                .orElseThrow(() -> new EntityNotFoundException("Owner not found"));

        String loginId = UUID.randomUUID().toString();
        String code = generate8Digits();
        String codeHash = sha256(code);

        OwnerLoginSession session = new OwnerLoginSession(
                user.getEmail(),
                codeHash,
                0
        );

        redisTemplate.opsForValue().set(
                RedisKeys.ownerLoginSession(loginId),
                session,
                TTL
        );

        emailService.sendEmail(
                user.getEmail(),
                "Your login code is: " + code,
                "Owner Login Verification"
        );

        return new OwnerLoginStartResponse(loginId);
    }

    public User verify(OwnerLoginVerifyRequest req) {

        String key = RedisKeys.ownerLoginSession(req.loginId());

        OwnerLoginSession session =
                (OwnerLoginSession) redisTemplate.opsForValue().get(key);

        if (session == null)
            throw new EntityNotFoundException("Invalid or expired login session");

        int attempts = session.attempts();

        if (attempts >= MAX_ATTEMPTS) {
            redisTemplate.delete(key);
            throw new IllegalStateException("Too many attempts");
        }

        attempts++;

        redisTemplate.opsForValue().set(
                key,
                new OwnerLoginSession(
                        session.email(),
                        session.codeHash(),
                        attempts
                ),
                TTL
        );

        if (!sha256(req.code()).equals(session.codeHash())) {

            if (attempts >= MAX_ATTEMPTS)
                redisTemplate.delete(key);

            throw new EntityNotFoundException("Invalid code");
        }

        User user = userRepo
                .findByEmailAndRolesContaining(session.email(), UserRole.ROLE_OWNER)
                .orElseThrow(() -> new EntityNotFoundException("Owner not found"));

        redisTemplate.delete(key);

        return user;
    }

    private String generate8Digits() {
        SecureRandom r = new SecureRandom();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 8; i++)
            sb.append(r.nextInt(10));
        return sb.toString();
    }

    private String sha256(String s) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(md.digest(s.getBytes()));
        } catch (Exception e) {
            throw new IllegalStateException("Hashing failed", e);
        }
    }
}