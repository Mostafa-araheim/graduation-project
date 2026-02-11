package com.example.pharma.service;


import com.example.pharma.dto.EmailSignUpRequest;
import com.example.pharma.dto.SignupResponse;
import com.example.pharma.dto.VerifySignupRequest;
import com.example.pharma.model.entity.core.User;
import com.example.pharma.model.entity.core.UserRole;
import com.example.pharma.repository.Core.UserRepository;
import com.example.pharma.util.RedisKeys;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.HexFormat;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SignupService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final UserRepository userRepo;
    private final EmailService emailService;

    private static final Duration TTL = Duration.ofMinutes(3);
    private static final int MAX_ATTEMPTS = 3;

    public SignupResponse startSignup(EmailSignUpRequest request, UserRole role) {
        ensureEmailNotRegistered(request.email());

        String signupId = UUID.randomUUID().toString();
        String code = generate8Digits();
        String codeHash = sha256(code);

        String key = RedisKeys.signupSession(signupId);

        redisTemplate.opsForHash().put(key, "email", request.email());
        redisTemplate.opsForHash().put(key, "userName", request.userName());
        redisTemplate.opsForHash().put(key, "codeHash", codeHash);
        redisTemplate.opsForHash().put(key, "attempts", "0");
        redisTemplate.opsForHash().put(key, "role", role.name()); // NEW

        redisTemplate.expire(key, TTL);

        emailService.sendEmail(
                request.email(),
                "Your verification code is: " + code,
                "Email Verification"
        );

        return new SignupResponse(signupId);
    }

    @Transactional
    public User confirmSignup(VerifySignupRequest req, UserRole expectedRole) {
        String key = RedisKeys.signupSession(req.signupId());

        Object emailObj = redisTemplate.opsForHash().get(key, "email");
        Object userNameObj = redisTemplate.opsForHash().get(key, "userName");
        Object codeHashObj = redisTemplate.opsForHash().get(key, "codeHash");
        Object attemptsObj = redisTemplate.opsForHash().get(key, "attempts");
        Object roleObj = redisTemplate.opsForHash().get(key, "role"); // NEW

        if (emailObj == null || userNameObj == null || codeHashObj == null || attemptsObj == null || roleObj == null) {
            throw new EntityNotFoundException("Invalid or expired signup session");
        }

        UserRole roleInSession = UserRole.valueOf(roleObj.toString());
        if (roleInSession != expectedRole) {
            throw new IllegalStateException("Wrong verification endpoint for this signup");
        }

        int attempts = Integer.parseInt(attemptsObj.toString());
        if (attempts >= MAX_ATTEMPTS) {
            redisTemplate.delete(key);
            throw new IllegalStateException("Too many attempts");
        }

        attempts++;
        redisTemplate.opsForHash().put(key, "attempts", String.valueOf(attempts));

        String expectedHash = codeHashObj.toString();
        String providedHash = sha256(req.code());

        if (!expectedHash.equals(providedHash)) {
            if (attempts >= MAX_ATTEMPTS) redisTemplate.delete(key);
            throw new EntityNotFoundException("Invalid code");
        }

        User user = new User();
        user.setEmail(emailObj.toString());
        user.setName(userNameObj.toString());
        user.setRoles(Set.of(roleInSession));
        userRepo.save(user);

        redisTemplate.delete(key);
        return user;
    }

    private void ensureEmailNotRegistered(String email) {
        if (userRepo.findByEmail(email).isPresent()) throw new IllegalStateException("Email already registered");
    }

    private String generate8Digits() {
        SecureRandom r = new SecureRandom();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 8; i++) sb.append(r.nextInt(10));
        return sb.toString();
    }

    private String sha256(String s) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(s.getBytes());
            return HexFormat.of().formatHex(digest);
        } catch (Exception e) {
            throw new IllegalStateException("Hashing failed", e);
        }
    }
}
