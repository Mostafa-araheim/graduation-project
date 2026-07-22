package com.example.pharma.service.cart;

import com.example.pharma.dto.cart.request.CartItemIdentifierRequest;
import com.example.pharma.dto.cart.response.CartItemResponse;
import com.example.pharma.dto.cart.response.CartResponse;
import com.example.pharma.exception.validation.BusinessRuleViolationException;
import com.example.pharma.mapper.cart.CartItemMapper;
import com.example.pharma.mapper.cart.CartMetadataMapper;
import com.example.pharma.model.cart.CartItem;
import com.example.pharma.model.cart.CartMetadata;
import com.example.pharma.model.entity.catalog.Product;
import com.example.pharma.model.entity.inventory.Inventory;
import com.example.pharma.model.entity.inventory.PharmacyProduct;
import com.example.pharma.model.entity.pharmacy.Pharmacy;
import com.example.pharma.repository.Catalog.ProductImageRepository;
import com.example.pharma.repository.Inventory.PharmacyProductRepository;
import com.example.pharma.repository.Pharmacy.PharmacyRepository;
import com.example.pharma.repository.cart.CartRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;
    @Mock
    private CartMetadataMapper cartMetadataMapper;
    @Mock
    private CartItemMapper cartItemMapper;
    @Mock
    private PharmacyProductRepository pharmacyProductRepository;
    @Mock
    private PharmacyRepository pharmacyRepository;
    @Mock
    private ProductImageRepository productImageRepository;

    @InjectMocks
    private CartService cartService;

    @Test
    void getCart_ShouldCalculateTotalCorrectly_WhenCartHasMultipleItems() {
        // ─── 1. Arrange
        Long cartId = 1L;
        Long userId = 100L;

        when(cartRepository.cartAccessible(cartId, userId)).thenReturn(true);

        CartMetadata metadata = CartMetadata.builder()
                .cartId(cartId)
                .userId(userId)
                .name("month ")
                .updatedAt(Instant.now())
                .build();
        when(cartRepository.getCartMetadata(cartId)).thenReturn(metadata);

        CartItem item1 = CartItem.builder().pharmacyProductId(10L).quantity(2L).build();
        CartItem item2 = CartItem.builder().pharmacyProductId(20L).quantity(3L).build();
        when(cartRepository.getAllCartItemsList(cartId)).thenReturn(List.of(item1, item2));

        Product p1 = new Product();
        p1.setProductId(1L);
        p1.setName("Panadol");

        PharmacyProduct pp1 = new PharmacyProduct();
        pp1.setPharmacyProductId(10L);
        pp1.setProduct(p1);
        pp1.setPrice(new BigDecimal("50.00"));

        when(pharmacyProductRepository.findById(10L)).thenReturn(Optional.of(pp1));
        when(productImageRepository.findPrimaryImageUrlByPharmacyProductId(10L)).thenReturn(Optional.of("http://img/p1.png"));
        
        CartItemResponse response1 = new CartItemResponse(
                10L, 2L, new BigDecimal("50.00"), new BigDecimal("100.00"), "http://img/p1.png", "Panadol"
        );
        when(cartItemMapper.toDto(item1, "http://img/p1.png", "Panadol")).thenReturn(response1);

        Product p2 = new Product();
        p2.setProductId(2L);
        p2.setName("Vitamin C");

        PharmacyProduct pp2 = new PharmacyProduct();
        pp2.setPharmacyProductId(20L);
        pp2.setProduct(p2);
        pp2.setPrice(new BigDecimal("30.00"));

        when(pharmacyProductRepository.findById(20L)).thenReturn(Optional.of(pp2));
        when(productImageRepository.findPrimaryImageUrlByPharmacyProductId(20L)).thenReturn(Optional.of("http://img/p2.png"));

        CartItemResponse response2 = new CartItemResponse(
                20L, 3L, new BigDecimal("30.00"), new BigDecimal("90.00"), "http://img/p2.png", "Vitamin C"
        );
        when(cartItemMapper.toDto(item2, "http://img/p2.png", "Vitamin C")).thenReturn(response2);

        // ─── 2. Act
        CartResponse result = cartService.getCart(cartId, userId);

        // ─── 3. Assert
        assertNotNull(result);
        assertEquals(cartId, result.cartId());
        assertEquals(5L, result.totalItems(), "إجمالي كمية العناصر يجب أن يساوي مجموع الكميات (2 + 3)");
        assertEquals(new BigDecimal("190.00"), result.totalPrice(), "إجمالي السعر يجب أن يساوي مجموع أسعار العناصر (100 + 90)");
        assertEquals(2, result.items().size());
    }

    @Test
    void addItem_ShouldThrowBusinessRuleViolationException_WhenItemIsOutOfStock() {
        // ─── 1. Arrange ───
        Long userId = 100L;
        Long productId = 10L;
        CartItemIdentifierRequest request = new CartItemIdentifierRequest(productId);

        PharmacyProduct outOfStockProduct = new PharmacyProduct();
        outOfStockProduct.setPharmacyProductId(productId);
        outOfStockProduct.setQuantity(0L);

        when(pharmacyProductRepository.findById(productId)).thenReturn(Optional.of(outOfStockProduct));

        // ─── 2. Act & 3. Assert ───
        BusinessRuleViolationException exception = assertThrows(
                BusinessRuleViolationException.class,
                () -> cartService.addItem(userId, request),
                "يجب رفض الإضافة إذا كان الرصيد صفر"
        );

        assertEquals("Item is out of stock", exception.getMessage());
        verify(cartRepository, never()).saveItem(anyLong(), any(CartItem.class));
        verify(cartRepository, never()).incrementQuantity(anyLong(), any(CartItemIdentifierRequest.class));
    }

    @Test
    void addItem_ShouldThrowBusinessRuleViolationException_WhenExistingCartQuantityEqualsOrExceedsStock() {
        // ─── 1. Arrange ───
        Long userId = 100L;
        Long productId = 10L;
        Long pharmacyId = 5L;
        CartItemIdentifierRequest request = new CartItemIdentifierRequest(productId);

        Pharmacy pharmacy = new Pharmacy();
        pharmacy.setPharmacyId(pharmacyId);
        pharmacy.setName("El Shifaa pharmacy");

        Inventory inventory = new Inventory();
        inventory.setPharmacy(pharmacy);

        PharmacyProduct productWithLimitedStock = new PharmacyProduct();
        productWithLimitedStock.setPharmacyProductId(productId);
        productWithLimitedStock.setQuantity(3L);
        productWithLimitedStock.setInventory(inventory);

        when(pharmacyProductRepository.findById(productId)).thenReturn(Optional.of(productWithLimitedStock));
        when(cartRepository.getUserCarts(userId)).thenReturn(Set.of(1L));

        CartMetadata metadata = CartMetadata.builder().cartId(1L).pharmacyId(pharmacyId).build();
        when(cartRepository.getCartMetadata(1L)).thenReturn(metadata);

        CartItem existingItem = CartItem.builder().pharmacyProductId(productId).quantity(3L).build();
        when(cartRepository.getItem(1L, request)).thenReturn(existingItem);

        // ─── 2. Act & 3. Assert ───
        BusinessRuleViolationException exception = assertThrows(
                BusinessRuleViolationException.class,
                () -> cartService.addItem(userId, request),
                "You can't increment the quantity if the existing quantity equals or exceeds the stock"
        );

        assertEquals("Not enough stock", exception.getMessage());
        verify(cartRepository, never()).incrementQuantity(anyLong(), any(CartItemIdentifierRequest.class));
    }
}
