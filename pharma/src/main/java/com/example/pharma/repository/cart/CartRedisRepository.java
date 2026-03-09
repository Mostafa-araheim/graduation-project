package com.example.pharma.repository.cart;

import com.example.pharma.dto.cart.request.CartItemIdentifierRequest;
import com.example.pharma.model.cart.CartItem;
import com.example.pharma.model.cart.CartMetadata;
import com.example.pharma.util.RedisKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class CartRedisRepository implements CartRepository {
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public Long createCart(CartMetadata metadata) {

        Long cartId = UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE;


        redisTemplate.opsForHash().putAll(RedisKeys.cart(cartId), Map.of(
                "userId", metadata.getUserId()
                , "name", metadata.getName()
                , "createdAt", metadata.getCreatedAt()
                , "updatedAt", metadata.getUpdatedAt()));

        return cartId;
    }

    @Override
    public void assignCartToUser(Long cartId, Long userId) {
        redisTemplate.opsForSet().add(RedisKeys.userCarts(userId), cartId);
    }

    @Override
    public void deleteCart(Long cartId) {
        Long userId = (Long) getMetadataField(cartId, "userId");
        redisTemplate.opsForSet()
                .remove(RedisKeys.userCarts(userId), cartId);
        redisTemplate.delete(RedisKeys.cart(cartId));
        redisTemplate.delete(RedisKeys.cartItems(cartId));
    }

    @Override
    public void saveItem(Long cartId, CartItem item) {

        String field = RedisKeys.cartItemField(
                item.getInventoryId(),
                item.getMedicineId()
        );

        redisTemplate.opsForHash()
                .put(RedisKeys.cartItems(cartId), field, item);
    }

    @Override
    public CartItem getItem(Long cartId, CartItemIdentifierRequest cartItemId) {

        String field = RedisKeys.cartItemField(cartItemId.inventoryId(), cartItemId.medicineId());

        Object value = redisTemplate.opsForHash()
                .get(RedisKeys.cartItems(cartId), field);

        return value != null ? (CartItem) value : null;
    }

    @Override
    public Object getMetadataField(Long cartId, String field) {

        return redisTemplate.opsForHash().get(RedisKeys.cart(cartId), field);
    }

    @Override
    public boolean exists(Long cartId) {

        return Boolean.TRUE.equals(
                redisTemplate.hasKey(RedisKeys.cart(cartId))
        );
    }

    @Override
    public void expire(Long cartId, Duration ttl) {

        redisTemplate.expire(RedisKeys.cart(cartId), ttl);
        redisTemplate.expire(RedisKeys.cartItems(cartId), ttl);
    }

    @Override
    public void updateQuantity(Long cartId,
                               CartItemIdentifierRequest cartItemId,
                               Long quantity) {

        CartItem item = getItem(cartId, cartItemId);

        if (item == null) return;

        item.setQuantity(quantity);

        saveItem(cartId, item);
    }

    @Override
    public void incrementQuantity(Long cartId,
                                  CartItemIdentifierRequest cartItemId) {

        CartItem item = getItem(cartId, cartItemId);

        if (item == null) return;

        updateQuantity(cartId,
                cartItemId,
                item.getQuantity() + 1);
    }

    @Override
    public void decrementQuantity(Long cartId,
                                  CartItemIdentifierRequest cartItemId) {

        CartItem item = getItem(cartId, cartItemId);

        if (item == null) return;

        updateQuantity(cartId,
                cartItemId,
                item.getQuantity() - 1);
    }

    @Override
    public void deleteItem(Long cartId,
                           CartItemIdentifierRequest cartItemId) {

        String field = RedisKeys.cartItemField(cartItemId.inventoryId(), cartItemId.medicineId());

        redisTemplate.opsForHash()
                .delete(RedisKeys.cartItems(cartId), field);
    }

    @Override
    public Map<String, CartItem> getAllCartItems(Long cartId) {

        Map<Object, Object> entries =
                redisTemplate.opsForHash()
                        .entries(RedisKeys.cartItems(cartId));

        return entries.entrySet().stream()
                .collect(Collectors.toMap(
                        e -> (String) e.getKey(),
                        e -> (CartItem) e.getValue()
                ));
    }

    @Override
    public Set<Long> getUserCarts(Long userId) {

        Set<Object> members =
                redisTemplate.opsForSet()
                        .members(RedisKeys.userCarts(userId));

        if (members == null) return Set.of();

        return members.stream()
                .map(v -> (Long) v)
                .collect(Collectors.toSet());
    }

    public List<CartItem> getAllCartItemsList(Long cartId) {

        return redisTemplate.opsForHash()
                .values(RedisKeys.cartItems(cartId))
                .stream()
                .map(v -> (CartItem) v)
                .toList();
    }

    public void clearCartItems(Long cartId) {
        redisTemplate.delete(RedisKeys.cartItems(cartId));
    }

    public CartMetadata getCartMetadata(Long cartId) {

        Map<Object, Object> entries =
                redisTemplate.opsForHash()
                        .entries(RedisKeys.cart(cartId));


        return CartMetadata.builder()
                .userId(((Number) entries.get("userId")).longValue())
                .name((String) entries.get("name"))
                .createdAt(Instant.parse((String) entries.get("createdAt")))
                .updatedAt(Instant.parse((String) entries.get("updatedAt")))
                .build();
    }

    @Override
    public Long saveCart(Long cartId, CartMetadata metadata) {

        if (cartId == null) {
            cartId = UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE;
        }

        redisTemplate.opsForHash().putAll(
                RedisKeys.cart(cartId),
                Map.of(
                        "userId", metadata.getUserId(),
                        "name", metadata.getName(),
                        "createdAt", metadata.getCreatedAt(),
                        "updatedAt", metadata.getUpdatedAt()
                )
        );

        return cartId;
    }
    @Override
    public void updateCartUpdatedAt(Long cartId, Instant updatedAt) {

        redisTemplate.opsForHash().put(
                RedisKeys.cart(cartId),
                "updatedAt",
                updatedAt
        );
    }
}