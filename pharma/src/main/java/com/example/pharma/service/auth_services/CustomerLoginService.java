package com.example.pharma.service.auth_services;

import com.example.pharma.dto.CustomerLoginSession;
import com.example.pharma.dto.CustomerLoginStartRequest;
import com.example.pharma.dto.CustomerLoginStartResponse;
import com.example.pharma.dto.CustomerLoginVerifyRequest;
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
public class CustomerLoginService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final UserRepository userRepo;
    private final EmailService emailService;

    private static final Duration TTL = Duration.ofMinutes(3);
    private static final int MAX_ATTEMPTS = 3;

    public CustomerLoginStartResponse start(CustomerLoginStartRequest req) {

        User user = userRepo
                .findByEmailAndRolesContaining(req.email(), UserRole.ROLE_CUSTOMER)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found"));

        String loginId = UUID.randomUUID().toString();
        String code = generate8Digits();
        String codeHash = sha256(code);

        CustomerLoginSession session = new CustomerLoginSession(
                user.getEmail(),
                codeHash,
                0
        );

        redisTemplate.opsForValue()
                .set(RedisKeys.customerLoginSession(loginId), session, TTL);

        emailService.sendEmail(
                user.getEmail(),
                "Your login code is: " + code,
                "Customer Login Verification"
        );

        return new CustomerLoginStartResponse(loginId);
    }

    public User verify(CustomerLoginVerifyRequest req) {

        String key = RedisKeys.customerLoginSession(req.loginId());

        CustomerLoginSession session =
                (CustomerLoginSession) redisTemplate.opsForValue().get(key);

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
                new CustomerLoginSession(
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
                .findByEmailAndRolesContaining(session.email(), UserRole.ROLE_CUSTOMER)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found"));

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
