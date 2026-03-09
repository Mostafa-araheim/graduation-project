package com.example.pharma.service.cart;

import com.example.pharma.dto.cart.request.*;
import com.example.pharma.dto.cart.response.CartItemResponse;
import com.example.pharma.dto.cart.response.CartResponse;
import com.example.pharma.exception.resource.EntityNotFoundException;
import com.example.pharma.exception.validation.BusinessRuleViolationException;
import com.example.pharma.mapper.cart.CartItemMapper;
import com.example.pharma.mapper.cart.CartMapper;
import com.example.pharma.model.cart.CartItem;
import com.example.pharma.model.cart.CartMetadata;
import com.example.pharma.model.entity.inventory.InventoryRecord;
import com.example.pharma.model.entity.inventory.InventoryRecordId;
import com.example.pharma.repository.Inventory.InventoryRecordRepository;
import com.example.pharma.repository.cart.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final CartMapper cartMapper;
    private final CartItemMapper cartItemMapper;
    private final InventoryRecordRepository inventoryRecordRepository;
    private static final Duration CART_TTL = Duration.ofDays(7);

    // i didn't check if the user id is existed or not because if the user is anonymous we can consider the session id
    // is the user id then after login the front end make some requests create another cart but for the real user and  copy items there then delete the anonymous one
    public CartResponse createCart(CreateCartRequest request) {

        CartMetadata cartMetadata = cartMapper.toEntity(request);

        Long cartId = cartRepository.createCart(cartMetadata);

        cartRepository.assignCartToUser(cartId, request.userId());

        cartRepository.expire(cartId, CART_TTL);

        return new CartResponse(
                cartId,
                List.of(),                 // items empty
                0L,                        // totalItems
                BigDecimal.ZERO,           // totalPrice
                cartMetadata.getCreatedAt() // updatedAt = createdAt
        );
    }

    public CartResponse getCart(Long cartId) {

        validateCart(cartId);

        CartMetadata metadata = cartRepository.getCartMetadata(cartId);

        List<CartItem> items =
                cartRepository.getAllCartItemsList(cartId);

        List<CartItemResponse> itemResponses =
                items.stream()
                        .map(cartItemMapper::toDto)
                        .toList();

        Long totalItems = items.stream()
                .mapToLong(CartItem::getQuantity)
                .sum();

        BigDecimal totalPrice = itemResponses.stream()
                .map(CartItemResponse::totalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new CartResponse(
                cartId,
                itemResponses,
                totalItems,
                totalPrice,
                metadata.getUpdatedAt()
        );
    }


    private void refreshCart(Long cartId) {

        cartRepository.expire(cartId, CART_TTL);

        cartRepository.updateCartUpdatedAt(cartId, Instant.now());
    }

    public void addItem(Long cartId, CartItemIdentifierRequest request) {

        validateCart(cartId);

        InventoryRecord inventoryRecord =
                inventoryRecordRepository.findById(
                        new InventoryRecordId(
                                request.inventoryId(),
                                request.medicineId()
                        )
                ).orElseThrow(() ->
                        new EntityNotFoundException("Inventory record not found"));

        if (inventoryRecord.getQuantity() <= 0) {
            throw new BusinessRuleViolationException("Item is out of stock");
        }

        CartItem existing =
                cartRepository.getItem(
                        cartId,
                        request
                );

        if (existing == null) {

            CartItem newItem = CartItem.builder()
                    .inventoryId(request.inventoryId())
                    .medicineId(request.medicineId())
                    .quantity(1L)
                    .pricePerUnit(inventoryRecord.getPrice())
                    .build();

            cartRepository.saveItem(cartId, newItem);

        } else {

            if (existing.getQuantity() >= inventoryRecord.getQuantity()) {
                throw new BusinessRuleViolationException("Not enough stock");
            }

            cartRepository.incrementQuantity(
                    cartId,
                    request
            );
        }

        refreshCart(cartId);
    }

    public void deleteItem(Long cartId, CartItemIdentifierRequest request) {

        validateCart(cartId);

        CartItem existing =
                cartRepository.getItem(
                        cartId,
                        request
                );

        if (existing == null) {
            throw new EntityNotFoundException("Item not found in cart");
        }

        cartRepository.deleteItem(
                cartId,
                request
        );

        refreshCart(cartId);
    }

    public void updateItemQuantity(Long cartId, UpdateCartItemQuantityRequest request) {

        validateCart(cartId);

        CartItem existing = cartRepository.getItem(
                cartId,
                new CartItemIdentifierRequest(request.inventoryId(), request.medicineId())
        );

        if (existing == null) {
            throw new EntityNotFoundException("Item not found in cart");
        }

        InventoryRecord inventoryRecord =
                inventoryRecordRepository.findById(
                        new InventoryRecordId(
                                request.inventoryId(),
                                request.medicineId()
                        )
                ).orElseThrow(() ->
                        new EntityNotFoundException("Inventory record not found"));

        if (request.quantity() > inventoryRecord.getQuantity()) {
            throw new BusinessRuleViolationException("Not enough stock");
        }

        cartRepository.updateQuantity(
                cartId,
                new CartItemIdentifierRequest(request.inventoryId(), request.medicineId()),
                request.quantity()
        );

        refreshCart(cartId);
    }

    public void increaseQuantity(Long cartId, CartItemIdentifierRequest request) {

        validateCart(cartId);

        CartItem existing =
                cartRepository.getItem(
                        cartId,
                        request
                );

        if (existing == null) {
            throw new EntityNotFoundException("Item not found in cart");
        }

        InventoryRecord inventoryRecord =
                inventoryRecordRepository.findById(
                        new InventoryRecordId(
                                request.inventoryId(),
                                request.medicineId()
                        )
                ).orElseThrow(() ->
                        new EntityNotFoundException("Inventory record not found"));

        if (existing.getQuantity() >= inventoryRecord.getQuantity()) {
            throw new BusinessRuleViolationException("Not enough stock");
        }

        cartRepository.incrementQuantity(
                cartId,
                request
        );

        refreshCart(cartId);
    }

    public void decreaseQuantity(Long cartId, CartItemIdentifierRequest request) {

        validateCart(cartId);

        CartItem existing = cartRepository.getItem(
                cartId,
                request
        );

        if (existing == null) {
            throw new EntityNotFoundException("Item not found in cart");
        }

        if (existing.getQuantity() <= 1) {

            cartRepository.deleteItem(
                    cartId,
                    request
            );

        } else {

            cartRepository.decrementQuantity(
                    cartId,
                    request
            );
        }

        refreshCart(cartId);
    }

    public void clearCart(Long cartId) {

        validateCart(cartId);

        cartRepository.clearCartItems(cartId);

        refreshCart(cartId);
    }

    public void deleteCart(Long cartId) {

        validateCart(cartId);

        cartRepository.deleteCart(cartId);
    }

    public Set<Long> getUserCarts(Long userId) {

        Set<Long> carts = cartRepository.getUserCarts(userId);

        return carts == null ? Set.of() : carts;
    }

    private void validateCart(Long cartId) {
        if (!cartRepository.exists(cartId)) {
            throw new EntityNotFoundException("Cart not found");
        }
    }

}
//POST   /carts
//GET    /carts/{cartId}
//POST   /carts/{cartId}/items

//DELETE /carts/{cartId}/items
//PATCH  /carts/{cartId}/items/quantity
//POST   /carts/{cartId}/items/increase
//POST   /carts/{cartId}/items/decrease
//DELETE /carts/{cartId}/clear