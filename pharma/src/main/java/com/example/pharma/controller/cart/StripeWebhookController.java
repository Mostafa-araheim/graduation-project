package com.example.pharma.controller.cart;

import com.example.pharma.config.stripe.StripeProperties;
import com.example.pharma.service.cart.StripeWebhookService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments/stripe")
@RequiredArgsConstructor
public class StripeWebhookController {

    private final StripeProperties stripeProperties;
    private final StripeWebhookService stripeWebhookService;

    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader
    ) {
        try {
            Event event = Webhook.constructEvent(
                    payload,
                    sigHeader,
                    stripeProperties.getWebhookSecret()
            );

            stripeWebhookService.handleEvent(event);
            return ResponseEntity.ok("Received");
        } catch (SignatureVerificationException e) {
            return ResponseEntity.badRequest().body("Invalid signature");
        }
    }

}
