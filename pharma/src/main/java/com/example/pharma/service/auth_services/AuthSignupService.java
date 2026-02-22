package com.example.pharma.service.auth_services;

import com.example.pharma.dto.auth.signup.SignupSession;
import com.example.pharma.dto.auth.signup.SignupStartRequest;
import com.example.pharma.dto.auth.signup.SignupStartResponse;
import com.example.pharma.dto.auth.signup.SignupVerifyRequest;
import com.example.pharma.model.entity.core.User;
import com.example.pharma.model.entity.core.UserRole;
import com.example.pharma.repository.Core.UserRepository;
import com.example.pharma.service.EmailService;
import com.example.pharma.util.RedisKeys;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
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
public class AuthSignupService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final UserRepository userRepo;
    private final EmailService emailService;

    private static final Duration TTL = Duration.ofMinutes(3);
    private static final int MAX_ATTEMPTS = 3;

    public SignupStartResponse start(SignupStartRequest req) {

        ensureEmailNotRegistered(req.email(), req.role());

        String signupId = UUID.randomUUID().toString();
        String code = generate8Digits();
        String codeHash = sha256(code);

        SignupSession session = new SignupSession(
                req.email(),
                req.name(),
                req.role(),
                codeHash,
                0
        );

        String key = RedisKeys.signupSession(signupId);
        redisTemplate.opsForValue().set(key, session, TTL);

        emailService.sendEmail(
                req.email(),
                "Your verification code is: " + code,
                "Email Verification"
        );

        return new SignupStartResponse(signupId, "Verification code sent");
    }

    @Transactional
    public User verify(SignupVerifyRequest req) {

        String key = RedisKeys.signupSession(req.signupId());

        SignupSession session =
                (SignupSession) redisTemplate.opsForValue().get(key);

        if (session == null)
            throw new EntityNotFoundException("Invalid or expired signup session");

        int attempts = session.attempts();

        if (attempts >= MAX_ATTEMPTS) {
            redisTemplate.delete(key);
            throw new IllegalStateException("Too many attempts");
        }

        attempts++;

        redisTemplate.opsForValue().set(
                key,
                new SignupSession(
                        session.email(),
                        session.name(),
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

        String email = session.email();
        String name = session.name();
        UserRole role = session.role();

        User user = userRepo.findByEmail(email).orElseGet(() -> {
            User u = new User();
            u.setEmail(email);
            u.setName(name);
            return u;
        });

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(name);
        }

        user.getRoles().add(role);

        userRepo.save(user);

        redisTemplate.delete(key);

        return user;
    }

    private void ensureEmailNotRegistered(String email, UserRole role) {
        if (userRepo.findByEmailAndRolesContaining(email, role).isPresent()) {
            throw new IllegalStateException("Email already registered for this role");
        }
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
