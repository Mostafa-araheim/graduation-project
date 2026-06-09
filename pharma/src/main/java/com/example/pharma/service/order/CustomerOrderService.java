package com.example.pharma.service.order;

import com.example.pharma.dto.cart.request.AssignCartToUserRequest;
import com.example.pharma.dto.cart.request.CartItemRequest;
import com.example.pharma.dto.cart.response.CartResponse;
import com.example.pharma.dto.order.response.CustomerOrderItemResponse;
import com.example.pharma.dto.order.response.CustomerOrderResponse;
import com.example.pharma.dto.user.AddressDto;
import com.example.pharma.exception.resource.EntityNotFoundException;
import com.example.pharma.model.entity.core.UserAddress;
import com.example.pharma.model.entity.inventory.PharmacyProduct;
import com.example.pharma.model.entity.order.Order;
import com.example.pharma.model.entity.order.OrderItem;
import com.example.pharma.repository.Inventory.PharmacyProductRepository;
import com.example.pharma.repository.Order.OrderRepository;
import com.example.pharma.service.cart.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerOrderService {

    private final OrderRepository orderRepository;
    private final PharmacyProductRepository pharmacyProductRepository;
    private final CartService cartService;

    @Transactional(readOnly = true)
    public Page<CustomerOrderResponse> getCustomerOrders(Long customerId, Pageable pageable) {
        Page<Order> orders = orderRepository.findByCustomer_UserId(customerId, pageable);
        return orders.map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public CustomerOrderResponse getCustomerOrderDetails(Long customerId, Long orderId) {
        Order order = orderRepository.findByOrderIdAndCustomer_UserId(orderId, customerId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found or access denied"));
        return mapToResponse(order);
    }

    @Transactional
    public CartResponse reorderFromOrder(Long customerId, Long orderId) {
        Order order = orderRepository.findByOrderIdAndCustomer_UserId(orderId, customerId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found or access denied"));

        List<CartItemRequest> cartItems = order.getItems().stream()
                .map(item -> {
                    PharmacyProduct pp = pharmacyProductRepository
                            .findByInventory_PharmacyIdAndProduct_ProductId(
                                    order.getPharmacy().getPharmacyId(),
                                    item.getProduct().getProductId()
                            )
                            .orElseThrow(() -> new EntityNotFoundException(
                                    "Product no longer available: " + item.getProduct().getName()
                            ));
                    return new CartItemRequest(pp.getPharmacyProductId(), (long) item.getQuantity());
                })
                .toList();

        AssignCartToUserRequest request = new AssignCartToUserRequest(cartItems);
        return cartService.assignSingleCartToUser(customerId, request);
    }

    private CustomerOrderResponse mapToResponse(Order order) {
        return new CustomerOrderResponse(
                order.getOrderId(),
                order.getPharmacy() != null ? order.getPharmacy().getPharmacyId() : null,
                order.getPharmacy() != null ? order.getPharmacy().getName() : null,
                order.getTotalPrice(),
                order.getDeliveryType(),
                order.getPaymentMethod(),
                order.getStatus(),
                order.getCreatedAt() != null ? order.getCreatedAt().getValue() : null,
                mapToAddressDto(order.getDeliveryAddress()),
                order.getItems().stream().map(this::mapToItemResponse).collect(Collectors.toList())
        );
    }

    private CustomerOrderItemResponse mapToItemResponse(OrderItem item) {
        return new CustomerOrderItemResponse(
                item.getProduct().getProductId(),
                item.getProduct().getName(),
                item.getQuantity(),
                item.getPriceAtPurchase(),
                item.getSubtotal()
        );
    }

    private AddressDto mapToAddressDto(UserAddress address) {
        if (address == null) return null;
        return new AddressDto(
                address.getUserAddressId(),
                address.getStreet(),
                address.getCity(),
                address.getPostalCode(),
                address.getCountry(),
                address.getApartmentNumber()
        );
    }
}
