// All code related to cart extracted into one file

// ================= CONTROLLER =================

// CartController.java
package com.example.pharma.controller.cart;

import com.example.pharma.dto.cart.request.AssignCartToUserRequest;
import com.example.pharma.dto.cart.request.CartItemIdentifierRequest;
import com.example.pharma.dto.cart.request.CreateCartRequest;
import com.example.pharma.dto.cart.request.CartItemQuantityRequest;
import com.example.pharma.dto.cart.response.CartResponse;
import com.example.pharma.dto.common.ApiResponse;
import com.example.pharma.service.cart.CartService;
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

    // DELETE /carts/{cartId}/items
    @DeleteMapping("/{cartId}/items")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ApiResponse<Void> deleteItem(
            @PathVariable Long cartId,
            @Valid @RequestBody CartItemIdentifierRequest request,
            @AuthenticationPrincipal(expression = "userId") Long userId
    ) {

        cartService.deleteItem(cartId, userId, request);

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
}

// ================= SERVICE =================

// CartService.java
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
import com.example.pharma.model.entity.inventory.PharmacyProduct;
import com.example.pharma.repository.Inventory.PharmacyProductRepository;
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
    private final PharmacyProductRepository pharmacyProductRepository;

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

        PharmacyProduct pharmacyProduct =
                getInventoryRecord(request.inventoryRecordId());

        if (pharmacyProduct.getQuantity() <= 0) {
            throw new BusinessRuleViolationException("Item is out of stock");
        }

        validateSamePharmacy(cartId, pharmacyProduct);

        CartItem existing = cartRepository.getItem(cartId, request);

        if (existing == null) {

            CartItem newItem = cartItemMapper.toEntity(pharmacyProduct);

            cartRepository.saveItem(cartId, newItem);

        } else {

            if (existing.getQuantity() >= pharmacyProduct.getQuantity()) {
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

        PharmacyProduct pharmacyProduct =
                getInventoryRecord(request.inventoryRecordId());

        if (request.quantity() > pharmacyProduct.getQuantity()) {
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

        PharmacyProduct pharmacyProduct =
                getInventoryRecord(request.inventoryRecordId());

        if (existing.getQuantity() >= pharmacyProduct.getQuantity()) {
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

            PharmacyProduct pharmacyProduct =
                    getInventoryRecord(itemRequest.inventoryRecordId());

            if (itemRequest.quantity() > pharmacyProduct.getQuantity()) {
                throw new BusinessRuleViolationException("Not enough stock");
            }

            CartItem item = cartItemMapper.toEntity(
                    pharmacyProduct,
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

    private PharmacyProduct getInventoryRecord(Long inventoryRecordId) {

        return pharmacyProductRepository.findById(inventoryRecordId)
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

    private void validateSamePharmacy(Long cartId, PharmacyProduct pharmacyProduct) {

        CartItem firstItem = cartRepository.getFirstCartItem(cartId);

        if (firstItem == null) {
            return;
        }

        PharmacyProduct firstRecord =
                getInventoryRecord(firstItem.getInventoryRecordId());

        Long cartPharmacyId =
                firstRecord.getInventory().getPharmacy().getPharmacyId();

        Long itemPharmacyId =
                pharmacyProduct.getInventory().getPharmacy().getPharmacyId();

        if (!cartPharmacyId.equals(itemPharmacyId)) {
            throw new BusinessRuleViolationException(
                    "Cart cannot contain items from multiple pharmacies"
            );
        }
    }
}

// ================= DTO REQUEST =================

// AssignCartToUserRequest.java
package com.example.pharma.dto.cart.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record AssignCartToUserRequest(

        @NotNull
        List<CartItemQuantityRequest> items,

        @NotBlank
        String cartName

) {}

// CartItemIdentifierRequest.java
package com.example.pharma.dto.cart.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CartItemIdentifierRequest(

        @NotNull(message = "InventoryId is required")
        @Positive(message = "InventoryId must be positive")
        Long inventoryRecordId

) {}

// CartItemQuantityRequest.java
package com.example.pharma.dto.cart.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CartItemQuantityRequest(

        @NotNull(message = "InventoryRecordId is required")
        @Positive(message = "InventoryRecordId must be positive")
        Long inventoryRecordId,

        @NotNull(message = "Quantity is required")
        @Positive(message = "Quantity must be greater than 0")
        Long quantity
) {}

// CreateCartRequest.java
package com.example.pharma.dto.cart.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCartRequest(


        @NotBlank(message = "Cart name cannot be blank")
        @Size(max = 100, message = "Cart name cannot exceed 100 characters")
        String name
) {}

// ================= DTO RESPONSE =================

// CartItemResponse.java
package com.example.pharma.dto.cart.response;

import java.math.BigDecimal;

public record CartItemResponse(
        Long productId,
        Long quantity,
        BigDecimal pricePerUnit,
        BigDecimal totalPrice
) {}

// CartResponse.java
package com.example.pharma.dto.cart.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record CartResponse(
        Long cartId,
        List<CartItemResponse> items,
        Long totalItems,
        BigDecimal totalPrice,
        Instant updatedAt
        ,String cartName
) {}

// ================= MAPPER =================

// CartItemMapper.java
package com.example.pharma.mapper.cart;

import com.example.pharma.dto.cart.response.CartItemResponse;
import com.example.pharma.model.cart.CartItem;
import com.example.pharma.model.entity.inventory.PharmacyProduct;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;

@Mapper(componentModel = "spring")
public interface CartItemMapper {

    @Mapping(target = "totalPrice", expression = "java(calculateTotal(cartItem))")
    CartItemResponse toDto(CartItem cartItem);

    default BigDecimal calculateTotal(CartItem cartItem) {
        if (cartItem == null || cartItem.getPricePerUnit() == null || cartItem.getQuantity() == null) {
            return BigDecimal.ZERO;
        }
        return cartItem.getPricePerUnit().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
    }

    @Mapping(target = "inventoryRecordId", source = "pharmacyProduct.pharmacyProductId")
    @Mapping(target = "quantity", expression = "java(1L)")
    @Mapping(target = "pricePerUnit", source = "pharmacyProduct.price")
    CartItem toEntity(PharmacyProduct pharmacyProduct);

    @Mapping(target = "inventoryRecordId", source = "pharmacyProduct.pharmacyProductId")
    @Mapping(target = "quantity", source = "quantity")
    @Mapping(target = "pricePerUnit", source = "pharmacyProduct.price")
    CartItem toEntity(PharmacyProduct pharmacyProduct, Long quantity);

}

// CartMetadataMapper.java
package com.example.pharma.mapper.cart;

import com.example.pharma.dto.cart.request.CreateCartRequest;
import com.example.pharma.model.cart.CartMetadata;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.Instant;

@Mapper(componentModel = "spring", imports = Instant.class)
public interface CartMetadataMapper {

    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "name", source = "request.name")
    @Mapping(target = "createdAt", expression = "java(Instant.now())")
    @Mapping(target = "updatedAt", expression = "java(Instant.now())")
    CartMetadata toEntity(Long userId, CreateCartRequest request);
}

// ================= MODEL =================

// CartItem.java
package com.example.pharma.model.cart;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartItem {

    private Long inventoryRecordId;

    private Long quantity;

    private BigDecimal pricePerUnit;
}

// CartMetadata.java
package com.example.pharma.model.cart;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartMetadata {
    private Long cartId;
    private Long userId;
    private String name;
    private Instant createdAt;
    private Instant updatedAt;
}

// ================= REPOSITORY =================

// CartRepository.java
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

// CartRedisRepository.java
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
        Long userId = ((Number) getMetadataField(cartId, "userId")).longValue();
        redisTemplate.opsForSet()
                .remove(RedisKeys.userCarts(userId), cartId);
        redisTemplate.delete(RedisKeys.cart(cartId));
        redisTemplate.delete(RedisKeys.cartItems(cartId));
    }

    @Override
    public void saveItem(Long cartId, CartItem item) {

        String field = RedisKeys.cartItemField(
                item.getInventoryRecordId()
        );

        redisTemplate.opsForHash()
                .put(RedisKeys.cartItems(cartId), field, item);
    }

    @Override
    public CartItem getItem(Long cartId, CartItemIdentifierRequest cartItemId) {

        String field = RedisKeys.cartItemField(cartItemId.inventoryRecordId());

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

        String field = RedisKeys.cartItemField(cartItemId.inventoryRecordId());

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

    @Override
    public boolean cartBelongsToUser(Long cartId, Long userId) {

        Object owner =
                redisTemplate.opsForHash()
                        .get(RedisKeys.cart(cartId), "userId");

        return owner != null && ((Number) owner).longValue() == userId;
    }

    @Override
    public CartItem getFirstCartItem(Long cartId) {

        Object value = redisTemplate.opsForHash()
                .values(RedisKeys.cartItems(cartId))
                .stream()
                .findFirst()
                .orElse(null);

        return value != null ? (CartItem) value : null;
    }

    @Override
    public boolean cartAccessible(Long cartId, Long userId) {

        Boolean cartExists = redisTemplate.hasKey(RedisKeys.cart(cartId));

        if (cartExists == null || !cartExists) {
            return false;
        }

        Boolean isOwner = redisTemplate.opsForSet()
                .isMember(RedisKeys.userCarts(userId), cartId);

        return Boolean.TRUE.equals(isOwner);
    }
}</content>
<parameter name="filePath">D:\coding\JAVA\graduation-project\pharma\cart_related_code.java
