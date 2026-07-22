package com.example.pharma.controller.auth;

import com.example.pharma.dto.auth.AuthVerification;
import com.example.pharma.dto.auth.AuthVerificationResult;
import com.example.pharma.dto.auth.signup.SignupStartRequest;
import com.example.pharma.dto.auth.signup.SignupStartResponse;
import com.example.pharma.dto.auth.signup.SignupVerifyRequest;
import com.example.pharma.model.entity.core.UserRole;
import com.example.pharma.model.entity.security.RefreshToken;
import com.example.pharma.security.jwt.JwtService;
import com.example.pharma.service.auth.AuthSignupService;
import com.example.pharma.service.auth.RefreshTokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthSignupController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class AuthSignupControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private AuthSignupService signupService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private RefreshTokenService refreshTokenService;

    @Test
    @DisplayName("يجب إرجاع 201 Created واستجابة نجاح عند إرسال طلب تسجيل حساب (Signup Start) بياناته صحيحة")
    void startSignup_ShouldReturn201Created_WhenRequestIsValid() throws Exception {
        // ─── 1. Arrange ───
        SignupStartRequest request = new SignupStartRequest(
                "ahmed@pharma.com",
                "Ahmed Ali",
                UserRole.ROLE_CUSTOMER
        );

        SignupStartResponse mockResponse = new SignupStartResponse("TEST_SIGNUP_ID_123", "Verification OTP sent");
        when(signupService.start(any(SignupStartRequest.class))).thenReturn(mockResponse);

        // ─── 2. Act & 3. Assert ───
        mockMvc.perform(post("/api/v1/auth/signup/start")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.signupId").value("TEST_SIGNUP_ID_123"))
                .andExpect(jsonPath("$.data.message").value("Verification OTP sent"));
    }

    @Test
    @DisplayName("يجب إرجاع 400 Bad Request عند إرسال إيميل أو اسم فارغ بسبب التحقق (Bean Validation)")
    void startSignup_ShouldReturn400BadRequest_WhenEmailOrNameIsBlank() throws Exception {
        // ─── 1. Arrange (طلب به إيميل واسم فارغان) ───
        SignupStartRequest invalidRequest = new SignupStartRequest(
                "",
                "",
                UserRole.ROLE_CUSTOMER
        );

        // ─── 2. Act & 3. Assert ───
        mockMvc.perform(post("/api/v1/auth/signup/start")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("يجب التحقق بنجاح وإرجاع 200 OK وكوكيز الـ Refresh Token عند إرسال كود التحقق السليم")
    void verifySignup_ShouldReturn200OkAndSetHeadersAndCookies_WhenCodeIsValid() throws Exception {
        // ─── 1. Arrange ───
        SignupVerifyRequest verifyRequest = new SignupVerifyRequest("TEST_SIGNUP_ID_123", "123456");

        AuthVerification authVerification = new AuthVerification(1L, "ahmed@pharma.com");
        AuthVerificationResult mockResult = new AuthVerificationResult(authVerification, "MOCK_JWT_TOKEN");

        when(signupService.verify(any(SignupVerifyRequest.class))).thenReturn(mockResult);

        RefreshToken mockRefreshToken = RefreshToken.builder()
                .token("MOCK_REFRESH_TOKEN")
                .build();
        when(refreshTokenService.createRefreshToken(1L)).thenReturn(mockRefreshToken);

        // ─── 2. Act & 3. Assert ───
        mockMvc.perform(post("/api/v1/auth/signup/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(verifyRequest)))
                .andExpect(status().isOk())
                .andExpect(header().string("Authorization", "Bearer MOCK_JWT_TOKEN"))
                .andExpect(cookie().value("refresh_token", "MOCK_REFRESH_TOKEN"))
                .andExpect(cookie().httpOnly("refresh_token", true))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.email").value("ahmed@pharma.com"));
    }
}
