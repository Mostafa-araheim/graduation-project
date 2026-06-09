package com.example.pharma.service.cart;

import com.example.pharma.exception.resource.EntityNotFoundException;
import com.example.pharma.model.entity.order.Order;
import com.example.pharma.model.entity.order.OrderStatus;
import com.example.pharma.model.entity.order.Payment;
import com.example.pharma.model.entity.order.PaymentStatus;
import com.example.pharma.repository.Order.OrderRepository;
import com.example.pharma.repository.Order.PaymentRepository;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class StripeWebhookService {

    private final PaymentRepository paymentRepository;
    private final CheckoutService checkoutService;
    private final OrderRepository orderRepository;

    @Transactional
    public void handleEvent(Event event) {
        if (event == null || event.getType() == null) {
            log.warn("Received null/invalid Stripe event");
            return;
        }

        switch (event.getType()) {
            case "checkout.session.completed" -> handleSessionCompleted(event);
            case "checkout.session.expired" -> handleSessionExpired(event);
            default -> log.info("Unhandled Stripe event type: {}", event.getType());
        }
    }

    private void handleSessionCompleted(Event event) {
        Session session = extractSession(event);

        Payment payment = paymentRepository
                .findByProviderPaymentIntentId(session.getId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Payment not found for Stripe Session: " + session.getId()
                ));

        if (payment.getOrder().getStatus() == OrderStatus.CANCELED ||
                payment.getOrder().getStatus() == OrderStatus.FAILED) {
            log.warn("Ignoring success event for terminal order state. orderId={}", payment.getOrder().getOrderId());
            return;
        }
        else if (payment.getStatus() == PaymentStatus.SUCCEEDED) {
            log.info("Stripe event already processed for sessionId={}", session.getId());
            return;
        }

        payment.setStatus(PaymentStatus.SUCCEEDED);
        payment.setPaidAt(Instant.now());
        payment.setFailureReason(null);
        payment.setClientSecret(null);
        paymentRepository.save(payment);

        Order order = payment.getOrder();
        checkoutService.finalizePaidOrder(order.getOrderId());

        log.info("Checkout session completed and order finalized. orderId={}, sessionId={}",
                order.getOrderId(), session.getId());
    }

    private void handleSessionExpired(Event event) {
        Session session = extractSession(event);

        Payment payment = paymentRepository
                .findByProviderPaymentIntentId(session.getId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Payment not found for Stripe Session: " + session.getId()
                ));

        updateOrderAndPaymentStatus(
                payment,
                PaymentStatus.CANCELED,
                OrderStatus.CANCELED,
                "Checkout session expired without payment"
        );

        log.warn("Checkout session expired. orderId={}, sessionId={}",
                payment.getOrder().getOrderId(), session.getId());
    }

    private Session extractSession(Event event) {
        return (Session) event.getDataObjectDeserializer()
                .getObject()
                .orElseThrow(() -> new IllegalStateException(
                        "Unable to deserialize Stripe event data for event: " + event.getId()
                ));
    }

    private void updateOrderAndPaymentStatus(Payment payment, PaymentStatus paymentStatus, OrderStatus orderStatus, String failureReason) {
        payment.setStatus(paymentStatus);
        payment.setFailureReason(failureReason);

        Order order = payment.getOrder();
        order.setStatus(orderStatus);

        orderRepository.save(order);
        paymentRepository.save(payment);
    }
}