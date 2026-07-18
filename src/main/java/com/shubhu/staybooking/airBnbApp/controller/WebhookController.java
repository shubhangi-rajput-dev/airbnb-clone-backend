package com.shubhu.staybooking.airBnbApp.controller;

import com.shubhu.staybooking.airBnbApp.service.BookingService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


/**
 * REST controller responsible for handling incoming Stripe webhook events.
 * <p>
 * Verifies the webhook signature before delegating supported payment events
 * to the booking service for further processing.
 */
@RestController
@RequestMapping("/webhook")
@RequiredArgsConstructor
public class WebhookController {

    /** Service responsible for processing booking payment events. */
    private final BookingService bookingService;

    @Value("${stripe.webhook.secret}")
    /* Stripe webhook signing secret used to verify webhook authenticity. */
    private String endpointSecret;

    /**
     * Receives Stripe payment webhook events.
     * <p>
     * Validates the Stripe signature and forwards verified events to the
     * booking service for business-specific payment processing.
     *
     * @param payload raw JSON payload received from Stripe
     * @param sigHeader Stripe signature header used for webhook verification
     * @return HTTP 200 when the webhook is processed successfully, or
     *         HTTP 400 if signature verification fails
     */
    @PostMapping("/payment")
    public ResponseEntity<?> capturePayment(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {
        try {
            // Verify the webhook signature to ensure the request originated from Stripe.
            Event event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
            // Delegate processing of the verified payment event to the service layer.
            bookingService.capturePayment(event);
            return ResponseEntity.ok().build();
        } catch (SignatureVerificationException e) {
            // Reject webhook requests with an invalid Stripe signature.
            return ResponseEntity.badRequest().body("Invalid signature");
        }
    }
}
