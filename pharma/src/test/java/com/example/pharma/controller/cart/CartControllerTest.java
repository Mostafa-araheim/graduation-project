package com.example.pharma.controller.cart;

import com.example.pharma.dto.cart.request.CartItemIdentifierRequest;
import com.example.pharma.dto.cart.request.CartItemRequest;
import com.example.pharma.dto.cart.response.CartResponse;
import com.example.pharma.security.AuthenticatedUser;
import com.example.pharma.service.cart.CartService;
import com.example.pharma.service.cart.CheckoutService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CartController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private CartService cartService;

    @MockitoBean
    private CheckoutService checkoutService;

    @MockitoBean
    private com.example.pharma.security.jwt.JwtService jwtService;

    @MockitoBean
    private com.example.pharma.service.auth.RefreshTokenService refreshTokenService;

    // مساعد لمحاكاة المستخدم المسجل (Customer) مع userId = 1L
    private RequestPostProcessor customerAuth() {
        AuthenticatedUser principal = new AuthenticatedUser(1L, "customer@pharma.com");
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                principal,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_CUSTOMER"))
        );
        return authentication(auth);
    }

    @Test
    @DisplayName("يجب إرجاع 200 OK وبيانات السلة بنجاح عند طلب سلة معينة")
    void getCart_ShouldReturn200OkAndCartData_WhenCartExists() throws Exception {
        // ─── 1. Arrange ───
        CartResponse mockCart = new CartResponse(
                10L,
                List.of(),
                2L,
                new BigDecimal("250.00"),
                Instant.now(),
                "سلة الأدوية الشهرية"
        );
        when(cartService.getCart(eq(10L), any())).thenReturn(mockCart);

        // ─── 2. Act & 3. Assert ───
        mockMvc.perform(get("/api/v1/carts/10")
                .with(customerAuth()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.cartId").value(10))
                .andExpect(jsonPath("$.data.cartName").value("سلة الأدوية الشهرية"))
                .andExpect(jsonPath("$.data.totalPrice").value(250.00));
    }

    @Test
    @DisplayName("يجب إرجاع 200 OK عند إضافة عنصر للسلة (addItem) ببيانات صحيحة")
    void addItem_ShouldReturn200Ok_WhenRequestIsValid() throws Exception {
        // ─── 1. Arrange ───
        CartItemIdentifierRequest request = new CartItemIdentifierRequest(5L);
        doNothing().when(cartService).addItem(any(), eq(request));

        // ─── 2. Act & 3. Assert ───
        mockMvc.perform(post("/api/v1/carts/items")
                .with(customerAuth())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Item added to cart"));
    }

    @Test
    @DisplayName("يجب إرجاع 400 Bad Request عند محاولة إضافة عنصر للسلة بدون تحديد pharmacyProductId")
    void addItem_ShouldReturn400BadRequest_WhenPharmacyProductIdIsNull() throws Exception {
        // ─── 1. Arrange (طلب فارغ بدون معرف منتج الصيدلية) ───
        CartItemIdentifierRequest invalidRequest = new CartItemIdentifierRequest(null);

        // ─── 2. Act & 3. Assert ───
        mockMvc.perform(post("/api/v1/carts/items")
                .with(customerAuth())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("يجب إرجاع 200 OK عند تحديث كمية عنصر داخل السلة (updateQuantity)")
    void updateQuantity_ShouldReturn200Ok_WhenRequestIsValid() throws Exception {
        // ─── 1. Arrange ───
        CartItemRequest request = new CartItemRequest(5L, 3L);
        doNothing().when(cartService).updateItemQuantity(eq(10L), any(), eq(request));

        // ─── 2. Act & 3. Assert ───
        mockMvc.perform(patch("/api/v1/carts/10/items/quantity")
                .with(customerAuth())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Item quantity updated"));
    }
}
