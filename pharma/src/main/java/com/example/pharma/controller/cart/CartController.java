package com.example.pharma.controller.cart;

import com.example.pharma.dto.cart.request.AssignCartToUserRequest;
import com.example.pharma.dto.cart.request.CartItemIdentifierRequest;
import com.example.pharma.dto.cart.request.CartItemQuantityRequest;
import com.example.pharma.dto.cart.request.CreateCartRequest;
import com.example.pharma.dto.cart.response.CartResponse;
import com.example.pharma.dto.common.ApiResponse;
import com.example.pharma.dto.order.request.CheckoutRequest;
import com.example.pharma.dto.order.respone.CheckoutResponse;
import com.example.pharma.service.cart.CartService;
import com.example.pharma.service.cart.CheckoutService;
import com.stripe.exception.StripeException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/carts")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final CheckoutService checkoutService;
    // POST   /carts
    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ApiResponse<CartResponse> createCart(
            @Valid @RequestBody CreateCartRequest request,
            @AuthenticationPrincipal(expression = "userId") Long userId) {

        CartResponse cartResponse = cartService.createCart(request, userId);

        return ApiResponse.success("Cart created successfully", cartResponse);
    }

    // GET    /carts/{cartId}
    @GetMapping("/{cartId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ApiResponse<CartResponse> getCart(
            @PathVariable Long cartId,
            @AuthenticationPrincipal(expression = "userId") Long userId
    ) {

        CartResponse cart = cartService.getCart(cartId, userId);

        return ApiResponse.success("Cart retrieved successfully", cart);
    }

    // GET    /carts/user
    @GetMapping("/user")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ApiResponse<List<CartResponse>> getUserCarts(
            @AuthenticationPrincipal(expression = "userId") Long userId
    ) {

        List<CartResponse> carts = cartService.getUserCarts(userId);

        return ApiResponse.success("User carts retrieved successfully", carts);
    }

    // POST   /carts/{cartId}/items
    @PostMapping("/{cartId}/items")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ApiResponse<Void> addItem(
            @PathVariable Long cartId,
            @Valid @RequestBody CartItemIdentifierRequest request,
            @AuthenticationPrincipal(expression = "userId") Long userId
    ) {

        cartService.addItem(cartId, userId, request);

        return ApiResponse.success("Item added to cart", null);
    }

    // PATCH  /carts/{cartId}/items/quantity
    @PatchMapping("/{cartId}/items/quantity")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ApiResponse<Void> updateQuantity(
            @PathVariable Long cartId,
            @Valid @RequestBody CartItemQuantityRequest request,
            @AuthenticationPrincipal(expression = "userId") Long userId
    ) {

        cartService.updateItemQuantity(cartId, userId, request);

        return ApiResponse.success("Item quantity updated", null);
    }

    // POST   /carts/{cartId}/items/increase
    @PostMapping("/{cartId}/items/increase")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ApiResponse<Void> increaseItemQuantity(
            @PathVariable Long cartId,
            @Valid @RequestBody CartItemIdentifierRequest request,
            @AuthenticationPrincipal(expression = "userId") Long userId
    ) {

        cartService.increaseQuantity(cartId, userId, request);

        return ApiResponse.success("Item quantity increased", null);
    }

    // POST   /carts/{cartId}/items/decrease
    @PostMapping("/{cartId}/items/decrease")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ApiResponse<Void> decreaseItemQuantity(
            @PathVariable Long cartId,
            @Valid @RequestBody CartItemIdentifierRequest request,
            @AuthenticationPrincipal(expression = "userId") Long userId
    ) {

        cartService.decreaseQuantity(cartId, userId, request);

        return ApiResponse.success("Item quantity decreased", null);
    }

    @DeleteMapping("/{cartId}/items/{pharmacyProductId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ApiResponse<Void> deleteItem(
            @PathVariable Long cartId,
            @PathVariable Long pharmacyProductId,
            @AuthenticationPrincipal(expression = "userId") Long userId
    ) {

        cartService.deleteItem(cartId, userId, pharmacyProductId);

        return ApiResponse.success("Item removed from cart", null);
    }

    // DELETE /carts/{cartId}/clear
    @DeleteMapping("/{cartId}/clear")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ApiResponse<Void> clearCart(
            @PathVariable Long cartId,
            @AuthenticationPrincipal(expression = "userId") Long userId
    ) {

        cartService.clearCart(cartId, userId);

        return ApiResponse.success("Cart cleared successfully", null);
    }

    // DELETE /carts/{cartId}
    @DeleteMapping("/{cartId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ApiResponse<Void> deleteCart(
            @PathVariable Long cartId,
            @AuthenticationPrincipal(expression = "userId") Long userId
    ) {

        cartService.deleteCart(cartId, userId);

        return ApiResponse.success("Cart deleted successfully", null);
    }

    // POST   /carts/assign
    @PostMapping("/assign")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ApiResponse<CartResponse> assignCartToUser(
            @Valid @RequestBody AssignCartToUserRequest request,
            @AuthenticationPrincipal(expression = "userId") Long userId
    ) {

        CartResponse cart = cartService.assignCartToUser(userId, request);

        return ApiResponse.success("Cart assigned successfully", cart);
    }

    @PostMapping("/{cartId}/checkout")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ApiResponse<CheckoutResponse> checkout(
            @PathVariable Long cartId,
            @Valid @RequestBody CheckoutRequest request,
            @AuthenticationPrincipal(expression = "userId") Long userId
    ) throws StripeException {
        CheckoutResponse response = checkoutService.checkout(cartId, userId, request);
        return ApiResponse.success("Checkout completed successfully", response);
    }
}