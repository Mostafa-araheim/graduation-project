package com.example.pharma.service.auth_services;

import com.example.pharma.dto.CustomerSignupSession;
import com.example.pharma.dto.CustomerSignupStartRequest;
import com.example.pharma.dto.CustomerSignupStartResponse;
import com.example.pharma.dto.CustomerSignupVerifyRequest;
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
public class CustomerSignupService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final UserRepository userRepo;
    private final EmailService emailService;

    private static final Duration TTL = Duration.ofMinutes(3);
    private static final int MAX_ATTEMPTS = 3;

    public CustomerSignupStartResponse start(CustomerSignupStartRequest req) {
        ensureEmailNotRegistered(req.email());

        String signupId = UUID.randomUUID().toString();

        String code = generate8Digits();
        String codeHash = sha256(code);

        CustomerSignupSession session = new CustomerSignupSession(
                req.email(),
                req.name(),
                codeHash,
                0
        );

        String key = RedisKeys.customerSignupSession(signupId);
        redisTemplate.opsForValue().set(key, session, TTL);

        emailService.sendEmail(
                req.email(),
                "Your verification code is: " + code,
                "Customer Email Verification"
        );

        return new CustomerSignupStartResponse(signupId);
    }

    @Transactional
    public User verify(CustomerSignupVerifyRequest req) {
        String key = RedisKeys.customerSignupSession(req.signupId());

        CustomerSignupSession session = (CustomerSignupSession) redisTemplate.opsForValue().get(key);
        if (session == null) throw new EntityNotFoundException("Invalid or expired signup session");

        int attempts = session.attempts();
        if (attempts >= MAX_ATTEMPTS) {
            redisTemplate.delete(key);
            throw new IllegalStateException("Too many attempts");
        }

        // increment attempts (by rewriting object)
        attempts++;
        CustomerSignupSession updated = new CustomerSignupSession(
                session.email(),
                session.name(),
                session.codeHash(),
                attempts
        );
        redisTemplate.opsForValue().set(key, updated, TTL);

        // verify code
        if (!sha256(req.code()).equals(session.codeHash())) {
            if (attempts >= MAX_ATTEMPTS) redisTemplate.delete(key);
            throw new EntityNotFoundException("Invalid code");
        }

        String email = session.email();
        String name  = session.name();

        // ✅ Upsert user by email (avoid duplicate email insert)
        User user = userRepo.findByEmail(email).orElseGet(() -> {
            User u = new User();
            u.setEmail(email);
            return u;
        });

        // optional: set/refresh name if missing
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(name);
        }

        // ✅ add CUSTOMER role without overriding other roles
        if (user.getRoles() == null) {
            user.setRoles(new java.util.HashSet<>());
        }
        user.getRoles().add(UserRole.ROLE_CUSTOMER);

        userRepo.save(user);

        // - create customer_profile row لو مش موجود
        // customerProfileRepo.saveIfNotExists(user.getUserId());

        redisTemplate.delete(key);
        return user;
    }
    private void ensureEmailNotRegistered(String email) {
        if (userRepo.findByEmailAndRolesContaining(email,UserRole.ROLE_CUSTOMER).isPresent()) {
            throw new IllegalStateException("Email already registered");
        }
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
            return HexFormat.of().formatHex(md.digest(s.getBytes()));
        } catch (Exception e) {
            throw new IllegalStateException("Hashing failed", e);
        }
    }
}
