package com.example.pharma.repository.cart;

import com.example.pharma.dto.cart.request.CartItemIdentifierRequest;
import com.example.pharma.model.cart.CartItem;
import com.example.pharma.model.cart.CartMetadata;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface CartRepository {

    // Create cart
    Long createCart(CartMetadata metadata);

    // Assign cart to user
    void assignCartToUser(Long cartId, Long userId);

    // Delete cart
    void deleteCart(Long cartId);

    // Add or set item
    void saveItem(Long cartId, CartItem item);

    // Update quantity explicitly
    public void updateQuantity(Long cartId, CartItemIdentifierRequest cartItemId, Long quantity);

    // Increment quantity
    public void incrementQuantity(Long cartId, CartItemIdentifierRequest cartItemId);

    public void decrementQuantity(Long cartId, CartItemIdentifierRequest cartItemId);

    // Delete item
    public void deleteItem(Long cartId, CartItemIdentifierRequest cartItemId);

    // Get specific item
    public CartItem getItem(Long cartId, CartItemIdentifierRequest cartItemId);

    // List all items
    public Map<String, CartItem> getAllCartItems(Long cartId);

    // Get cart metadata field
    Object getMetadataField(Long cartId, String field);

    // List all user carts
    Set<Long> getUserCarts(Long userId);

    // Exists
    boolean exists(Long cartId);

    // Expire
    void expire(Long cartId, Duration ttl);

    public List<CartItem> getAllCartItemsList(Long cartId);

    public void clearCartItems(Long cartId);

    public CartMetadata getCartMetadata(Long cartId);

    public Long saveCart(Long cartId, CartMetadata metadata);

    void updateCartUpdatedAt(Long cartId, Instant updatedAt);

    boolean cartBelongsToUser(Long cartId, Long userId);

    CartItem getFirstCartItem(Long cartId);

    boolean cartAccessible(Long cartId, Long userId);
}