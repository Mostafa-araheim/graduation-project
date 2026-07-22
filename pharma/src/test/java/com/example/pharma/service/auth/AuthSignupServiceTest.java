package com.example.pharma.service.auth;

import com.example.pharma.dto.auth.AuthVerificationResult;
import com.example.pharma.dto.auth.signup.SignupSession;
import com.example.pharma.dto.auth.signup.SignupStartRequest;
import com.example.pharma.dto.auth.signup.SignupStartResponse;
import com.example.pharma.dto.auth.signup.SignupVerifyRequest;
import com.example.pharma.exception.resource.EntityAlreadyExistsException;
import com.example.pharma.model.entity.core.CustomerProfile;
import com.example.pharma.model.entity.core.User;
import com.example.pharma.model.entity.core.UserRole;
import com.example.pharma.repository.Core.CustomerProfileRepository;
import com.example.pharma.repository.Core.OwnerProfileRepository;
import com.example.pharma.repository.Core.UserRepository;
import com.example.pharma.security.jwt.JwtService;
import com.example.pharma.service.EmailService;
import com.example.pharma.util.RedisKeys;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.core.Authentication;

import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthSignupServiceTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    @Mock
    private ValueOperations<String, Object> valueOperations;
    @Mock
    private UserRepository userRepo;
    @Mock
    private EmailService emailService;
    @Mock
    private CustomerProfileRepository customerProfileRepository;
    @Mock
    private OwnerProfileRepository ownerProfileRepository;
    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthSignupService authSignupService;

    @Test
    void start_ShouldThrowEntityAlreadyExistsException_WhenEmailAlreadyRegistered() {
        // ─── 1. Arrange ───
        SignupStartRequest request = new SignupStartRequest("test@pharma.com", "Ahmed", UserRole.ROLE_CUSTOMER);
        User existingUser = new User();
        existingUser.setEmail("test@pharma.com");

        when(userRepo.findByEmailAndRolesContaining("test@pharma.com", UserRole.ROLE_CUSTOMER))
                .thenReturn(Optional.of(existingUser));

        // ─── 2. Act & 3. Assert ───
        assertThrows(EntityAlreadyExistsException.class, () -> authSignupService.start(request));

        verifyNoInteractions(emailService, redisTemplate);
    }

    @Test
    void start_ShouldSaveSessionInRedisAndSendEmail_WhenValidRequest() {
        // ─── 1. Arrange ───
        SignupStartRequest request = new SignupStartRequest("moatzahmed010@gmail.com", "Mostafa", UserRole.ROLE_CUSTOMER);
        when(userRepo.findByEmailAndRolesContaining("moatzahmed010@gmail.com", UserRole.ROLE_CUSTOMER))
                .thenReturn(Optional.empty());
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        // ─── 2. Act ───
        SignupStartResponse response = authSignupService.start(request);

        // ─── 3. Assert ───
        assertNotNull(response);
        assertNotNull(response.signupId());
        assertEquals("Verification code sent", response.message());


        verify(valueOperations, times(1)).set(anyString(), any(SignupSession.class), any());

        verify(emailService, times(1)).sendEmail(eq("moatzahmed010@gmail.com"), contains("Your verification code is:"), eq("Email Verification"));
    }

    @Test
    void verify_ShouldCreateCustomerAndReturnJwt_WhenCodeIsValid() throws Exception {
        // ─── 1. Arrange ───
        String signupId = UUID.randomUUID().toString();
        String code = "12345678";

        MessageDigest md = MessageDigest.getInstance("SHA-256");
        String codeHash = HexFormat.of().formatHex(md.digest(code.getBytes()));

        SignupSession session = new SignupSession("moatzahmed010@gmail.com", "moatz", UserRole.ROLE_CUSTOMER, codeHash, 0L);
        SignupVerifyRequest request = new SignupVerifyRequest(signupId, code);
        String redisKey = RedisKeys.signupSession(signupId);

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(redisKey)).thenReturn(session);
        when(userRepo.findByEmail("moatzahmed010@gmail.com")).thenReturn(Optional.empty());

        User savedUser = new User();
        savedUser.setUserId(100L);
        savedUser.setEmail("moatzahmed010@gmail.com");
        savedUser.setName("moatz");
        savedUser.getRoles().add(UserRole.ROLE_CUSTOMER);

        when(userRepo.save(any(User.class))).thenReturn(savedUser);
        when(jwtService.generateToken(any(Authentication.class))).thenReturn("mock.jwt.token");

        // ─── 2. Act ───
        AuthVerificationResult result = authSignupService.verify(request);

        // ─── 3. Assert ───
        assertNotNull(result);
        assertEquals("mock.jwt.token", result.jwt());
        assertEquals("moatzahmed010@gmail.com", result.user().email());
        assertEquals(100L, result.user().userId());


        verify(customerProfileRepository, times(1)).save(any(CustomerProfile.class));

        verify(redisTemplate, times(1)).delete(redisKey);
    }
}