package com.example.pharma.service.cart;

import com.example.pharma.dto.order.request.CheckoutRequest;
import com.example.pharma.dto.order.response.CheckoutItemResponse;
import com.example.pharma.dto.order.response.CheckoutResponse;
import com.example.pharma.exception.access.AccessDeniedException;
import com.example.pharma.exception.resource.EntityNotFoundException;
import com.example.pharma.exception.validation.BusinessRuleViolationException;
import com.example.pharma.model.cart.CartItem;
import com.example.pharma.model.cart.CartMetadata;
import com.example.pharma.model.entity.core.CustomerProfile;
import com.example.pharma.model.entity.core.UserAddress;
import com.example.pharma.model.entity.inventory.PharmacyProduct;
import com.example.pharma.model.entity.order.*;
import com.example.pharma.model.entity.pharmacy.Pharmacy;
import com.example.pharma.repository.Core.CustomerProfileRepository;
import com.example.pharma.repository.Core.UserAddressRepository;
import com.example.pharma.repository.Inventory.PharmacyProductRepository;
import com.example.pharma.repository.Order.OrderRepository;
import com.example.pharma.repository.Order.PaymentRepository;
import com.example.pharma.repository.cart.CartRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CheckoutService {

    private final CartRepository cartRepository;
    private final PharmacyProductRepository pharmacyProductRepository;
    private final CustomerProfileRepository customerProfileRepository;
    private final UserAddressRepository userAddressRepository;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final StripePaymentService stripePaymentService;


    private record CheckoutDraft(
            Long orderId,
            Long paymentId,
            Long cartId,
            Long pharmacyId,
            DeliveryType deliveryType,
            PaymentMethod paymentMethod,
            OrderStatus orderStatus,
            BigDecimal totalPrice,
            String currency,
            List<CheckoutItemResponse> items
    ) {}
    @Transactional
    public CheckoutResponse checkout(Long cartId, Long userId, CheckoutRequest request) throws StripeException {
        CheckoutDraft draft = createCheckoutDraft(cartId, userId, request);

        if (request.paymentMethod() == PaymentMethod.CARD) {
            PaymentIntent intent = stripePaymentService.createPaymentIntent(
                    draft.totalPrice(),
                    draft.currency(),
                    draft.orderId()
            );

            Payment savedPayment = attachStripePaymentIntent(
                    draft.paymentId(),
                    intent.getId(),
                    intent.getClientSecret()
            );

            return new CheckoutResponse(
                    draft.orderId(),
                    draft.cartId(),
                    draft.pharmacyId(),
                    draft.deliveryType(),
                    draft.paymentMethod(),
                    draft.orderStatus(),
                    draft.totalPrice(),
                    savedPayment.getCurrency(),
                    true,
                    savedPayment.getStatus(),
                    savedPayment.getClientSecret(),
                    draft.items()
            );
        }

        finalizePaidOrder(draft.orderId());

        Payment cashPayment = markCashPaymentPending(draft.paymentId());

        return new CheckoutResponse(
                draft.orderId(),
                draft.cartId(),
                draft.pharmacyId(),
                draft.deliveryType(),
                draft.paymentMethod(),
                OrderStatus.PLACED,
                draft.totalPrice(),
                cashPayment.getCurrency(),
                false,
                cashPayment.getStatus(),
                null,
                draft.items()
        );
    }

    @Transactional
    public CheckoutDraft createCheckoutDraft(Long cartId, Long userId, CheckoutRequest request) {
        validateCartAccess(cartId, userId);

        CartMetadata cartMetadata = cartRepository.getCartMetadata(cartId);
        if (cartMetadata == null) {
            throw new EntityNotFoundException("Cart not found");
        }

        if (orderRepository.existsBySourceCartId(cartId)) {
            throw new BusinessRuleViolationException("This cart was already checked out");
        }

        validateDeliveryRequirements(request, userId);

        List<CartItem> cartItems = cartRepository.getAllCartItemsList(cartId);
        if (cartItems == null || cartItems.isEmpty()) {
            throw new BusinessRuleViolationException("Cannot checkout an empty cart");
        }

        CustomerProfile customer = customerProfileRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Customer profile not found"));

        Order order = new Order();
        order.setCustomer(customer);
        order.setSourceCartId(cartId);
        order.setDeliveryType(request.deliveryType());
        order.setPaymentMethod(request.paymentMethod());

        List<OrderItem> orderItems = new ArrayList<>();
        List<CheckoutItemResponse> responseItems = new ArrayList<>();
        BigDecimal totalPrice = BigDecimal.ZERO;
        Pharmacy pharmacy = null;

        for (CartItem cartItem : cartItems) {
            PharmacyProduct pharmacyProduct = pharmacyProductRepository.findById(cartItem.getPharmacyProductId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Pharmacy product not found: " + cartItem.getPharmacyProductId()
                    ));

            if (pharmacyProduct.getQuantity() == null || pharmacyProduct.getQuantity() <= 0) {
                throw new BusinessRuleViolationException(
                        "Item is out of stock: " + pharmacyProduct.getPharmacyProductId()
                );
            }

            if (cartItem.getQuantity() == null || cartItem.getQuantity() <= 0) {
                throw new BusinessRuleViolationException("Invalid cart item quantity");
            }

            if (pharmacyProduct.getQuantity() < cartItem.getQuantity()) {
                throw new BusinessRuleViolationException(
                        "Not enough stock for pharmacy product id " + pharmacyProduct.getPharmacyProductId()
                );
            }

            Pharmacy currentPharmacy = pharmacyProduct.getInventory().getPharmacy();

            if (pharmacy == null) {
                pharmacy = currentPharmacy;
            } else if (!pharmacy.getPharmacyId().equals(currentPharmacy.getPharmacyId())) {
                throw new BusinessRuleViolationException("Cart cannot contain items from multiple pharmacies");
            }

            BigDecimal unitPrice = pharmacyProduct.getPrice();
            BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(cartItem.getQuantity()));

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(pharmacyProduct.getProduct());
            orderItem.setQuantity(Math.toIntExact(cartItem.getQuantity()));
            orderItem.setPriceAtPurchase(unitPrice);
            orderItem.setSubtotal(subtotal);

            orderItems.add(orderItem);

            responseItems.add(new CheckoutItemResponse(
                    pharmacyProduct.getProduct().getProductId(),
                    pharmacyProduct.getProduct().getName(),
                    pharmacyProduct.getPharmacyProductId(),
                    cartItem.getQuantity(),
                    unitPrice,
                    subtotal
            ));

            totalPrice = totalPrice.add(subtotal);
        }

        order.setPharmacy(pharmacy);
        order.setItems(orderItems);
        order.setTotalPrice(totalPrice);
        order.setStatus(resolveInitialOrderStatus());

        Order savedOrder;
        try {
            savedOrder = orderRepository.save(order);
        } catch (DataIntegrityViolationException ex) {
            throw new BusinessRuleViolationException("This cart was already checked out");
        }

        Payment payment = new Payment();
        payment.setOrder(savedOrder);
        payment.setAmount(totalPrice);
        payment.setCurrency("egp");
        payment.setIdempotencyKey(UUID.randomUUID().toString());

        if (request.paymentMethod() == PaymentMethod.CASH) {
            payment.setProviderPaymentIntentId("CASH_" + UUID.randomUUID());
            payment.setStatus(PaymentStatus.PENDING_CASH);
            payment.setClientSecret(null);
        } else {
            payment.setStatus(PaymentStatus.INITIATED);
            payment.setClientSecret(null);
            payment.setProviderPaymentIntentId(null);
        }

        Payment savedPayment = paymentRepository.save(payment);

        return new CheckoutDraft(
                savedOrder.getOrderId(),
                savedPayment.getPaymentId(),
                cartId,
                pharmacy.getPharmacyId(),
                savedOrder.getDeliveryType(),
                savedOrder.getPaymentMethod(),
                savedOrder.getStatus(),
                savedOrder.getTotalPrice(),
                savedPayment.getCurrency(),
                responseItems
        );
    }

    @Transactional
    public Payment attachStripePaymentIntent(Long paymentId, String providerPaymentIntentId, String clientSecret) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new EntityNotFoundException("Payment not found"));

        payment.setProviderPaymentIntentId(providerPaymentIntentId);
        payment.setClientSecret(clientSecret);
        payment.setStatus(PaymentStatus.INITIATED);

        return paymentRepository.save(payment);
    }

    @Transactional
    public Payment markCashPaymentPending(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new EntityNotFoundException("Payment not found"));

        payment.setStatus(PaymentStatus.PENDING_CASH);
        return paymentRepository.save(payment);
    }


    @Transactional
    public void finalizePaidOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));

        if (order.getStatus() == OrderStatus.PLACED) {
            return;
        }

        for (OrderItem item : order.getItems()) {
            int updatedRows = pharmacyProductRepository.decrementStockIfEnough(
                    order.getPharmacy().getPharmacyId(),
                    item.getProduct().getProductId(),
                    item.getQuantity()
            );

            if (updatedRows == 0) {
                throw new BusinessRuleViolationException(
                        "Insufficient stock while finalizing order for product id " + item.getProduct().getProductId()
                );
            }
        }

        if (cartRepository.exists(order.getSourceCartId())) {
            cartRepository.deleteCart(order.getSourceCartId());
        }

        order.setStatus(OrderStatus.PLACED);
        orderRepository.save(order);
    }

    private void validateCartAccess(Long cartId, Long userId) {
        if (!cartRepository.cartAccessible(cartId, userId)) {
            throw new AccessDeniedException("You are not allowed to access this cart");
        }
    }

    private void validateDeliveryRequirements(CheckoutRequest request, Long userId) {
        if (request.deliveryType() == DeliveryType.DELIVERY) {
            if (request.deliveryAddressId() == null) {
                throw new BusinessRuleViolationException("Delivery address is required for delivery orders");
            }

            UserAddress address = userAddressRepository.findById(request.deliveryAddressId())
                    .orElseThrow(() -> new EntityNotFoundException("Delivery address not found"));

            if (address.getUser() == null || !address.getUser().getUserId().equals(userId)) {
                throw new AccessDeniedException("You are not allowed to use this delivery address");
            }
        }
    }

    private OrderStatus resolveInitialOrderStatus() {
        return  OrderStatus.PENDING_PAYMENT ;
    }
}