package com.example.pharma.service;

import com.example.pharma.model.cart.CartItem;
import com.example.pharma.model.cart.CartMetadata;
import com.example.pharma.util.RedisKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {

    private final RedisTemplate<String, Object> redisTemplate;

    //user:{userId}:carts
//→ Set of cart IDs
//
//cart:{cartId}
//→ Hash containing cart metadata (user_id, name, created_at, updated_at, status)
//
//cart:{cartId}:items
//→ Hash where:
//  - Key = productId
//  - Value = JSON string containing {product_id, quantity, price_per_unit, added_at}
    public Long createCart(Long userId, String name) {
        Long cartId = UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE;

        CartMetadata cart = CartMetadata.builder()
                .userId(userId)
                .name(name)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();


        redisTemplate.opsForHash().putAll(
                RedisKeys.cart(cartId),
                Map.of(
                        "userId", cart.getUserId(),
                        "name", cart.getName(),
                        "createdAt", cart.getCreatedAt(),
                        "updatedAt", cart.getUpdatedAt()
                )
        );

        // user:{userId}:carts
        redisTemplate.opsForSet()
                .add(RedisKeys.userCarts(userId), cartId);
        setCartTTL(cartId, Duration.ofDays(7));
        return cartId;
    }

    public void addOrUpdateItem(Long cartId,
                                Long productId,
                                int quantity,
                                BigDecimal pricePerUnit) {

        CartItem item = CartItem.builder()
                .productId(productId)
                .quantity(quantity)
                .pricePerUnit(pricePerUnit)
                .addedAt(Instant.now())
                .build();

        // cart:{cartId}:items
        redisTemplate.opsForHash().put(
                RedisKeys.cartItems(cartId),
                productId.toString(),
                item
        );

        // update cart timestamp
        redisTemplate.opsForHash().put(
                RedisKeys.cart(cartId),
                "updatedAt",
                Instant.now()
        );
    }

    public void removeItem(Long cartId, Long productId) {
        redisTemplate.opsForHash().delete(
                RedisKeys.cartItems(cartId),
                productId.toString()
        );

        redisTemplate.opsForHash().put(
                RedisKeys.cart(cartId),
                "updatedAt",
                Instant.now()
        );
    }

    public List<CartItem> getCartItems(Long cartId) {
        Map<Object, Object> entries =
                redisTemplate.opsForHash().entries(
                        RedisKeys.cartItems(cartId)
                );

        return entries.values().stream()
                .map(v -> (CartItem) v)
                .toList();
    }

    public Set<Long> getUserCarts(Long userId) {
        return redisTemplate.opsForSet()
                .members(RedisKeys.userCarts(userId))
                .stream()
                .map(id -> (Long) id)
                .collect(Collectors.toSet());
    }

    public void setCartTTL(Long cartId, Duration ttl) {
        redisTemplate.expire(RedisKeys.cart(cartId), ttl);
        redisTemplate.expire(RedisKeys.cartItems(cartId), ttl);
    }

}