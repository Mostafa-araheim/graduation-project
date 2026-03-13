package com.example.pharma.service.auth;

import com.example.pharma.dto.auth.login.LoginSession;
import com.example.pharma.dto.auth.login.LoginStartRequest;
import com.example.pharma.dto.auth.login.LoginStartResponse;
import com.example.pharma.dto.auth.login.LoginVerifyRequest;
import com.example.pharma.exception.access.IllegalStateException;
import com.example.pharma.exception.resource.EntityNotFoundException;
import com.example.pharma.model.entity.core.User;
import com.example.pharma.repository.Core.UserRepository;
import com.example.pharma.service.EmailService;
import com.example.pharma.util.RedisKeys;
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
public class AuthLoginService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final UserRepository userRepo;
    private final EmailService emailService;

    private static final Duration TTL = Duration.ofMinutes(3);
    private static final int MAX_ATTEMPTS = 3;

    public LoginStartResponse start(LoginStartRequest req) {

        User user = userRepo
                .findByEmailAndRolesContaining(req.email(), req.role())
                .orElseThrow(() ->
                        new EntityNotFoundException("User not found for this role"));

        String loginId = UUID.randomUUID().toString();
        String code = generate8Digits();
        String codeHash = sha256(code);

        LoginSession session = new LoginSession(
                user.getEmail(),
                req.role(),
                codeHash,
                0L
        );

        redisTemplate.opsForValue()
                .set(RedisKeys.loginSession(loginId), session, TTL);

        emailService.sendEmail(
                user.getEmail(),
                "Your login code is: " + code,
                "Login Verification"
        );

        return new LoginStartResponse(loginId, "Verification code sent");
    }

    public User verify(LoginVerifyRequest req) {

        String key = RedisKeys.loginSession(req.loginId());

        LoginSession session =
                (LoginSession) redisTemplate.opsForValue().get(key);

        if (session == null)
            throw new EntityNotFoundException("Invalid or expired login session");

        Long attempts = session.attempts();

        if (attempts >= MAX_ATTEMPTS) {
            redisTemplate.delete(key);
            throw new IllegalStateException("Too many attempts");
        }

        attempts++;

        redisTemplate.opsForValue().set(
                key,
                new LoginSession(
                        session.email(),
                        session.role(),
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
                .findByEmailAndRolesContaining(session.email(), session.role())
                .orElseThrow(() ->
                        new EntityNotFoundException("User not found"));

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
            throw new IllegalStateException("Hashing failed :" + e);
        }
    }
}
