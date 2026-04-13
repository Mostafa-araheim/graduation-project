package com.example.pharma.service.cart;

import com.example.pharma.exception.resource.EntityNotFoundException;
import com.example.pharma.model.entity.order.Order;
import com.example.pharma.model.entity.order.Payment;
import com.example.pharma.model.entity.order.PaymentStatus;
import com.example.pharma.repository.Order.PaymentRepository;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
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

    @Transactional
    public void handleEvent(Event event) {
        if (event == null || event.getType() == null) {
            log.warn("Received null/invalid Stripe event");
            return;
        }

        switch (event.getType()) {
            case "payment_intent.succeeded" -> handlePaymentIntentSucceeded(event);
            case "payment_intent.payment_failed" -> handlePaymentIntentFailed(event);
            case "payment_intent.canceled" -> handlePaymentIntentCanceled(event);
            case "payment_intent.requires_action" -> handlePaymentIntentRequiresAction(event);
            default -> log.info("Unhandled Stripe event type: {}", event.getType());
        }
    }

    private void handlePaymentIntentSucceeded(Event event) {
        PaymentIntent paymentIntent = extractPaymentIntent(event);

        Payment payment = paymentRepository
                .findByProviderPaymentIntentId(paymentIntent.getId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Payment not found for Stripe PaymentIntent: " + paymentIntent.getId()
                ));

        if (payment.getStatus() == PaymentStatus.SUCCEEDED) {
            log.info("Stripe event already processed for paymentIntent={}", paymentIntent.getId());
            return;
        }

        payment.setStatus(PaymentStatus.SUCCEEDED);
        payment.setPaidAt(Instant.now());
        payment.setFailureReason(null);
        payment.setClientSecret(paymentIntent.getClientSecret());
        paymentRepository.save(payment);

        Order order = payment.getOrder();
        checkoutService.finalizePaidOrder(order.getOrderId());

        log.info("Payment succeeded and order finalized. orderId={}, paymentIntent={}",
                order.getOrderId(), paymentIntent.getId());
    }

    private void handlePaymentIntentFailed(Event event) {
        PaymentIntent paymentIntent = extractPaymentIntent(event);

        Payment payment = paymentRepository
                .findByProviderPaymentIntentId(paymentIntent.getId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Payment not found for Stripe PaymentIntent: " + paymentIntent.getId()
                ));

        payment.setStatus(PaymentStatus.FAILED);
        payment.setFailureReason(
                paymentIntent.getLastPaymentError() != null
                        ? paymentIntent.getLastPaymentError().getMessage()
                        : "Stripe payment failed"
        );

        paymentRepository.save(payment);

        log.warn("Payment failed. orderId={}, paymentIntent={}, reason={}",
                payment.getOrder().getOrderId(), paymentIntent.getId(), payment.getFailureReason());
    }

    private void handlePaymentIntentCanceled(Event event) {
        PaymentIntent paymentIntent = extractPaymentIntent(event);

        Payment payment = paymentRepository
                .findByProviderPaymentIntentId(paymentIntent.getId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Payment not found for Stripe PaymentIntent: " + paymentIntent.getId()
                ));

        payment.setStatus(PaymentStatus.CANCELED);
        payment.setFailureReason("Payment was canceled on Stripe");
        paymentRepository.save(payment);

        log.warn("Payment canceled. orderId={}, paymentIntent={}",
                payment.getOrder().getOrderId(), paymentIntent.getId());
    }

    private void handlePaymentIntentRequiresAction(Event event) {
        PaymentIntent paymentIntent = extractPaymentIntent(event);

        Payment payment = paymentRepository
                .findByProviderPaymentIntentId(paymentIntent.getId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Payment not found for Stripe PaymentIntent: " + paymentIntent.getId()
                ));

        payment.setStatus(PaymentStatus.REQUIRES_ACTION);
        payment.setClientSecret(paymentIntent.getClientSecret());
        paymentRepository.save(payment);

        log.info("Payment requires action. orderId={}, paymentIntent={}",
                payment.getOrder().getOrderId(), paymentIntent.getId());
    }

    private PaymentIntent extractPaymentIntent(Event event) {
        return (PaymentIntent) event.getDataObjectDeserializer()
                .getObject()
                .orElseThrow(() -> new IllegalStateException(
                        "Unable to deserialize Stripe event data for event: " + event.getId()
                ));
    }
}