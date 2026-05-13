package com.example.pharma.service.cart;

import com.example.pharma.dto.cart.request.AssignCartToUserRequest;
import com.example.pharma.dto.cart.request.CartItemIdentifierRequest;
import com.example.pharma.dto.cart.request.CartItemRequest;
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
import com.example.pharma.model.entity.pharmacy.Pharmacy;
import com.example.pharma.repository.Catalog.ProductImageRepository;
import com.example.pharma.repository.Inventory.PharmacyProductRepository;
import com.example.pharma.repository.Pharmacy.PharmacyRepository;
import com.example.pharma.repository.cart.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartMetadataMapper cartMetadataMapper;
    private final CartItemMapper cartItemMapper;
    private final PharmacyProductRepository pharmacyProductRepository;
    private final PharmacyRepository pharmacyRepository;
    private final ProductImageRepository productImageRepository;


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

    public CartResponse getCart(Long cartId, Long userId) {

        validateCartAccess(cartId, userId);

        CartMetadata metadata = cartRepository.getCartMetadata(cartId);

        List<CartItem> items = cartRepository.getAllCartItemsList(cartId);

        List<CartItemResponse> itemResponses =
                items.stream()
                        .map(item -> {
                            String imageUrl = productImageRepository
                                    .findPrimaryImageUrlByPharmacyProductId(item.getPharmacyProductId())
                                    .orElse(null);
                            PharmacyProduct pharmacyProduct = getPharmacyProduct(item.getPharmacyProductId());
                            String productName = pharmacyProduct.getProduct().getName();
                            return cartItemMapper.toDto(item, imageUrl,productName);
                        })
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
    public void addItem(Long userId, CartItemIdentifierRequest request) {
        PharmacyProduct pharmacyProduct = getPharmacyProduct(request.pharmacyProductId());

        if (pharmacyProduct.getQuantity() <= 0) {
            throw new BusinessRuleViolationException("Item is out of stock");
        }

        Long pharmacyId = pharmacyProduct.getInventory().getPharmacy().getPharmacyId();

        Long cartId = findOrCreateCartForUserAndPharmacy(userId, pharmacyId);

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

    private Long findOrCreateCartForUserAndPharmacy(Long userId, Long pharmacyId) {
        List<Long> userCartIds = new ArrayList<>(cartRepository.getUserCarts(userId)) ;

        for (Long cartId : userCartIds) {
            CartMetadata cart = cartRepository.getCartMetadata(cartId);

            if (cart != null && pharmacyId.equals(cart.getPharmacyId())) {
                return cartId;
            }
        }
        Pharmacy pharmacy = pharmacyRepository.findById(pharmacyId).orElseThrow(() -> new EntityNotFoundException("Pharmacy not found"));
        CartResponse newCart = createCart(
                new CreateCartRequest(pharmacy.getName(),pharmacyId)
                , userId);

        return newCart.cartId();
    }

    // PATCH /carts/{cartId}/items/quantity
    public void updateItemQuantity(Long cartId, Long userId, CartItemRequest request) {

        validateCartAccess(cartId, userId);

        getExistingCartItem(
                cartId,
                new CartItemIdentifierRequest(request.pharmacyProductId())
        );

        PharmacyProduct pharmacyProduct =
                getPharmacyProduct(request.pharmacyProductId());

        if (request.quantity() > pharmacyProduct.getQuantity()) {
            throw new BusinessRuleViolationException("Not enough stock");
        }

        cartRepository.updateQuantity(
                cartId,
                new CartItemIdentifierRequest(request.pharmacyProductId()),
                request.quantity()
        );

        refreshCart(cartId);
    }

    // POST /carts/{cartId}/items/increase
    public void increaseQuantity(Long cartId, Long userId, CartItemIdentifierRequest request) {

        validateCartAccess(cartId, userId);

        CartItem existing = getExistingCartItem(cartId, request);

        PharmacyProduct pharmacyProduct =
                getPharmacyProduct(request.pharmacyProductId());

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

    public void deleteItem(Long cartId, Long userId, Long pharmacyProductId) {

        validateCartAccess(cartId, userId);

        CartItemIdentifierRequest request =
                new CartItemIdentifierRequest(pharmacyProductId);

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

    private PharmacyProduct getPharmacyProduct(Long pharmacyProductId) {

        return pharmacyProductRepository.findById(pharmacyProductId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Pharmacy product not found"));
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
                getPharmacyProduct(firstItem.getPharmacyProductId());

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

    public CartResponse assignSingleCartToUser(Long userId, AssignCartToUserRequest request) {

        if (userId == null) {
            throw new ValidationException("UserId is required");
        }

        if (request.items() == null || request.items().isEmpty()) {
            throw new ValidationException("Cart items are required");
        }

        Map<Long, Long> requestedQuantities = new LinkedHashMap<>();
        Map<Long, PharmacyProduct> pharmacyProducts = new LinkedHashMap<>();

        Long pharmacyId = null;

        for (CartItemRequest itemRequest : request.items()) {

            Long pharmacyProductId = itemRequest.pharmacyProductId();
            Long requestedQuantity = itemRequest.quantity();

            PharmacyProduct pharmacyProduct = pharmacyProductRepository
                    .findWithDetailsById(pharmacyProductId)
                    .orElseThrow(() ->
                            new EntityNotFoundException("Pharmacy product not found"));

            if (pharmacyProduct.getQuantity() <= 0) {
                throw new BusinessRuleViolationException("Item is out of stock");
            }

            Long currentPharmacyId =
                    pharmacyProduct.getPharmacy().getPharmacyId();

            if (pharmacyId == null) {
                pharmacyId = currentPharmacyId;
            } else if (!pharmacyId.equals(currentPharmacyId)) {
                throw new BusinessRuleViolationException(
                        "Cart cannot contain items from multiple pharmacies"
                );
            }

            requestedQuantities.merge(
                    pharmacyProductId,
                    requestedQuantity,
                    Long::sum
            );

            pharmacyProducts.put(pharmacyProductId, pharmacyProduct);
        }

        for (Map.Entry<Long, Long> entry : requestedQuantities.entrySet()) {

            PharmacyProduct pharmacyProduct = pharmacyProducts.get(entry.getKey());

            if (entry.getValue() > pharmacyProduct.getQuantity()) {
                throw new BusinessRuleViolationException("Not enough stock");
            }
        }

        Long cartId = findOrCreateCartForUserAndPharmacy(userId, pharmacyId);

        for (Map.Entry<Long, Long> entry : requestedQuantities.entrySet()) {

            Long pharmacyProductId = entry.getKey();
            Long quantityToAdd = entry.getValue();

            PharmacyProduct pharmacyProduct = pharmacyProducts.get(pharmacyProductId);

            CartItemIdentifierRequest identifier =
                    new CartItemIdentifierRequest(pharmacyProductId);

            CartItem existingItem = cartRepository.getItem(cartId, identifier);

            if (existingItem == null) {

                CartItem newItem = CartItem.builder()
                        .pharmacyProductId(pharmacyProductId)
                        .quantity(quantityToAdd)
                        .pricePerUnit(pharmacyProduct.getPrice())
                        .build();

                cartRepository.saveItem(cartId, newItem);

            } else {

                Long newQuantity = existingItem.getQuantity() + quantityToAdd;



                cartRepository.updateQuantity(cartId, identifier, newQuantity);
            }
        }

        refreshCart(cartId);

        return getCart(cartId, userId);
    }

    public List<CartResponse> assignCartToUser(
            Long userId,
            List<AssignCartToUserRequest> requests
    ) {

        if (userId == null) {
            throw new ValidationException("UserId is required");
        }

        if (requests == null || requests.isEmpty()) {
            throw new ValidationException("Cart requests are required");
        }

        return requests.stream()
                .map(request -> assignSingleCartToUser(userId, request))
                .toList();
    }
}