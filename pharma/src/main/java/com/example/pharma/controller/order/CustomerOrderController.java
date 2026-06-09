package com.example.pharma.controller.order;

import com.example.pharma.dto.cart.response.CartResponse;
import com.example.pharma.dto.common.ApiResponse;
import com.example.pharma.dto.order.response.CustomerOrderResponse;
import com.example.pharma.service.order.CustomerOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class CustomerOrderController {

    private final CustomerOrderService customerOrderService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<Page<CustomerOrderResponse>> getMyOrders(
            @AuthenticationPrincipal(expression = "userId") Long userId,
            Pageable pageable
    ) {
        Page<CustomerOrderResponse> orders = customerOrderService.getCustomerOrders(userId, pageable);
        return ApiResponse.success("Orders retrieved successfully", orders);
    }

    @GetMapping("/{orderId}")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<CustomerOrderResponse> getOrderDetails(
            @AuthenticationPrincipal(expression = "userId") Long userId,
            @PathVariable Long orderId
    ) {
        CustomerOrderResponse order = customerOrderService.getCustomerOrderDetails(userId, orderId);
        return ApiResponse.success("Order details retrieved successfully", order);
    }

    @PostMapping("/{orderId}/reorder")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ApiResponse<CartResponse> reorder(
            @AuthenticationPrincipal(expression = "userId") Long userId,
            @PathVariable Long orderId
    ) {
        CartResponse cart = customerOrderService.reorderFromOrder(userId, orderId);
        return ApiResponse.success("Items re-added to cart successfully", cart);
    }
}
