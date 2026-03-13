package com.example.pharma.service.cart;

import com.example.pharma.dto.cart.request.AssignCartToUserRequest;
import com.example.pharma.dto.cart.request.CartItemIdentifierRequest;
import com.example.pharma.dto.cart.request.CartItemQuantityRequest;
import com.example.pharma.dto.cart.request.CreateCartRequest;
import com.example.pharma.dto.cart.response.CartItemResponse;
import com.example.pharma.dto.cart.response.CartResponse;
import com.example.pharma.exception.access.AccessDeniedException;
import com.example.pharma.exception.resource.EntityNotFoundException;
import com.example.pharma.exception.validation.BusinessRuleViolationException;
import com.example.pharma.exception.validation.ValidationException;
import com.example.pharma.mapper.cart.CartItemMapper;
import com.example.pharma.mapper.cart.CartMetadataMapper;
import com.example.pharma.model.cart.CartItem;
import com.example.pharma.model.cart.CartMetadata;
import com.example.pharma.model.entity.inventory.InventoryRecord;
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
    private final CartMetadataMapper cartMetadataMapper;
    private final CartItemMapper cartItemMapper;
    private final InventoryRecordRepository inventoryRecordRepository;

    private static final Duration CART_TTL = Duration.ofDays(7);

    // POST /carts
    public CartResponse createCart(CreateCartRequest request, Long userId) {

        CartMetadata cartMetadata = cartMetadataMapper.toEntity(userId, request);

        Long cartId = cartRepository.createCart(cartMetadata);

        cartRepository.assignCartToUser(cartId, userId);

        cartRepository.expire(cartId, CART_TTL);

        return new CartResponse(
                cartId,
                List.of(),
                0L,
                BigDecimal.ZERO,
                cartMetadata.getCreatedAt(),
                request.name()
        );
    }

    // GET /carts/{cartId}
    public CartResponse getCart(Long cartId, Long userId) {

        validateCartAccess(cartId, userId);

        CartMetadata metadata = cartRepository.getCartMetadata(cartId);

        List<CartItem> items = cartRepository.getAllCartItemsList(cartId);

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
                metadata.getUpdatedAt(),
                metadata.getName()
        );
    }

    // GET /carts/user
    public List<CartResponse> getUserCarts(Long userId) {

        if (userId == null) {
            throw new ValidationException("UserId is required");
        }

        Set<Long> cartIds = cartRepository.getUserCarts(userId);

        if (cartIds == null || cartIds.isEmpty()) {
            return List.of();
        }

        return cartIds.stream()
                .map(id -> getCart(id, userId))
                .toList();
    }

    // POST /carts/{cartId}/items
    public void addItem(Long cartId, Long userId, CartItemIdentifierRequest request) {

        validateCartAccess(cartId, userId);

        InventoryRecord inventoryRecord =
                getInventoryRecord(request.inventoryRecordId());

        if (inventoryRecord.getQuantity() <= 0) {
            throw new BusinessRuleViolationException("Item is out of stock");
        }

        validateSamePharmacy(cartId, inventoryRecord);

        CartItem existing = cartRepository.getItem(cartId, request);

        if (existing == null) {

            CartItem newItem = cartItemMapper.toEntity(inventoryRecord);

            cartRepository.saveItem(cartId, newItem);

        } else {

            if (existing.getQuantity() >= inventoryRecord.getQuantity()) {
                throw new BusinessRuleViolationException("Not enough stock");
            }

            cartRepository.incrementQuantity(cartId, request);
        }

        refreshCart(cartId);
    }

    // PATCH /carts/{cartId}/items/quantity
    public void updateItemQuantity(Long cartId, Long userId, CartItemQuantityRequest request) {

        validateCartAccess(cartId, userId);

        getExistingCartItem(
                cartId,
                new CartItemIdentifierRequest(request.inventoryRecordId())
        );

        InventoryRecord inventoryRecord =
                getInventoryRecord(request.inventoryRecordId());

        if (request.quantity() > inventoryRecord.getQuantity()) {
            throw new BusinessRuleViolationException("Not enough stock");
        }

        cartRepository.updateQuantity(
                cartId,
                new CartItemIdentifierRequest(request.inventoryRecordId()),
                request.quantity()
        );

        refreshCart(cartId);
    }

    // POST /carts/{cartId}/items/increase
    public void increaseQuantity(Long cartId, Long userId, CartItemIdentifierRequest request) {

        validateCartAccess(cartId, userId);

        CartItem existing = getExistingCartItem(cartId, request);

        InventoryRecord inventoryRecord =
                getInventoryRecord(request.inventoryRecordId());

        if (existing.getQuantity() >= inventoryRecord.getQuantity()) {
            throw new BusinessRuleViolationException("Not enough stock");
        }

        cartRepository.incrementQuantity(cartId, request);

        refreshCart(cartId);
    }

    // POST /carts/{cartId}/items/decrease
    public void decreaseQuantity(Long cartId, Long userId, CartItemIdentifierRequest request) {

        validateCartAccess(cartId, userId);

        CartItem existing = getExistingCartItem(cartId, request);

        if (existing.getQuantity() <= 1) {

            cartRepository.deleteItem(cartId, request);

        } else {

            cartRepository.decrementQuantity(cartId, request);
        }

        refreshCart(cartId);
    }

    // DELETE /carts/{cartId}/items
    public void deleteItem(Long cartId, Long userId, CartItemIdentifierRequest request) {

        validateCartAccess(cartId, userId);

        getExistingCartItem(cartId, request);

        cartRepository.deleteItem(cartId, request);

        refreshCart(cartId);
    }

    // DELETE /carts/{cartId}/clear
    public void clearCart(Long cartId, Long userId) {

        validateCartAccess(cartId, userId);

        cartRepository.clearCartItems(cartId);

        refreshCart(cartId);
    }

    // DELETE /carts/{cartId}
    public void deleteCart(Long cartId, Long userId) {

        validateCartAccess(cartId, userId);

        cartRepository.deleteCart(cartId);
    }

    // POST /carts/assign
    public CartResponse assignCartToUser(Long userId, AssignCartToUserRequest request) {

        if (userId == null) {
            throw new ValidationException("UserId is required");
        }

        CartMetadata metadata = cartMetadataMapper.toEntity(
                userId,
                new CreateCartRequest(request.cartName())
        );

        Long cartId = cartRepository.createCart(metadata);

        cartRepository.assignCartToUser(cartId, userId);

        for (CartItemQuantityRequest itemRequest : request.items()) {

            InventoryRecord inventoryRecord =
                    getInventoryRecord(itemRequest.inventoryRecordId());

            if (itemRequest.quantity() > inventoryRecord.getQuantity()) {
                throw new BusinessRuleViolationException("Not enough stock");
            }

            CartItem item = cartItemMapper.toEntity(
                    inventoryRecord,
                    itemRequest.quantity()
            );

            cartRepository.saveItem(cartId, item);
        }

        cartRepository.expire(cartId, CART_TTL);

        return getCart(cartId, userId);
    }

    // ================= PRIVATE HELPERS =================

    private void refreshCart(Long cartId) {

        cartRepository.expire(cartId, CART_TTL);

        cartRepository.updateCartUpdatedAt(cartId, Instant.now());
    }

    private void validateCartAccess(Long cartId, Long userId) {

        if (!cartRepository.cartAccessible(cartId, userId)) {
            throw new AccessDeniedException("You are not allowed to access this cart");
        }
    }

    private InventoryRecord getInventoryRecord(Long inventoryRecordId) {

        return inventoryRecordRepository.findById(inventoryRecordId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Inventory record not found"));
    }

    private CartItem getExistingCartItem(Long cartId, CartItemIdentifierRequest request) {

        CartItem item = cartRepository.getItem(cartId, request);

        if (item == null) {
            throw new EntityNotFoundException("Item not found in cart");
        }

        return item;
    }

    private void validateSamePharmacy(Long cartId, InventoryRecord inventoryRecord) {

        CartItem firstItem = cartRepository.getFirstCartItem(cartId);

        if (firstItem == null) {
            return;
        }

        InventoryRecord firstRecord =
                getInventoryRecord(firstItem.getInventoryRecordId());

        Long cartPharmacyId =
                firstRecord.getInventory().getPharmacy().getPharmacyId();

        Long itemPharmacyId =
                inventoryRecord.getInventory().getPharmacy().getPharmacyId();

        if (!cartPharmacyId.equals(itemPharmacyId)) {
            throw new BusinessRuleViolationException(
                    "Cart cannot contain items from multiple pharmacies"
            );
        }
    }
}