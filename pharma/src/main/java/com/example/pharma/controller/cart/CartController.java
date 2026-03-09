package com.example.pharma.controller.cart;

import com.example.pharma.dto.cart.request.CartItemIdentifierRequest;
import com.example.pharma.dto.cart.request.CreateCartRequest;
import com.example.pharma.dto.cart.request.UpdateCartItemQuantityRequest;
import com.example.pharma.dto.cart.response.CartResponse;
import com.example.pharma.dto.common.ApiResponse;
import com.example.pharma.service.cart.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/carts")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    // POST /carts
    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ApiResponse<CartResponse> createCart(
            @Valid @RequestBody CreateCartRequest request
    ) {

        CartResponse cartResponse = cartService.createCart(request);

        return ApiResponse.success("Cart created successfully", cartResponse);
    }


    // GET /carts/{cartId}
    @GetMapping("/{cartId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ApiResponse<CartResponse> getCart(
            @PathVariable Long cartId
    ) {

        CartResponse cart = cartService.getCart(cartId);

        return ApiResponse.success("Cart retrieved successfully", cart);
    }


    // POST /carts/{cartId}/items
    @PostMapping("/{cartId}/items")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ApiResponse<Void> addItem(
            @PathVariable Long cartId,
            @Valid @RequestBody CartItemIdentifierRequest request
    ) {

        cartService.addItem(cartId, request);

        return ApiResponse.success("Item added to cart", null);
    }

    // DELETE /carts/{cartId}/items
    @DeleteMapping("/{cartId}/items")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ApiResponse<Void> deleteItem(
            @PathVariable Long cartId,
            @Valid @RequestBody CartItemIdentifierRequest request
    ) {

        cartService.deleteItem(cartId, request);

        return ApiResponse.success("Item removed from cart", null);
    }

    // PATCH /carts/{cartId}/items/quantity
    @PatchMapping("/{cartId}/items/quantity")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ApiResponse<Void> updateQuantity(
            @PathVariable Long cartId,
            @Valid @RequestBody UpdateCartItemQuantityRequest request
    ) {

        cartService.updateItemQuantity(cartId, request);

        return ApiResponse.success("Item quantity updated", null);
    }

    // POST /carts/{cartId}/items/increase
    @PostMapping("/{cartId}/items/increase")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ApiResponse<Void> increaseItemQuantity(
            @PathVariable Long cartId,
            @Valid @RequestBody CartItemIdentifierRequest request
    ) {

        cartService.increaseQuantity(cartId, request);

        return ApiResponse.success("Item quantity increased", null);
    }

    // POST /carts/{cartId}/items/decrease
    @PostMapping("/{cartId}/items/decrease")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ApiResponse<Void> decreaseItemQuantity(
            @PathVariable Long cartId,
            @Valid @RequestBody CartItemIdentifierRequest request
    ) {

        cartService.decreaseQuantity(cartId, request);

        return ApiResponse.success("Item quantity decreased", null);
    }

    // DELETE /carts/{cartId}/clear
    @DeleteMapping("/{cartId}/clear")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ApiResponse<Void> clearCart(
            @PathVariable Long cartId
    ) {

        cartService.clearCart(cartId);

        return ApiResponse.success("Cart cleared successfully", null);
    }
}