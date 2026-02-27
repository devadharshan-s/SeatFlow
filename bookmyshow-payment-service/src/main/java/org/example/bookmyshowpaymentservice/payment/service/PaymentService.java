package org.example.bookmyshowpaymentservice.payment.service;

import org.example.bookmyshowpaymentservice.payment.api.dto.CreatePaymentRequest;
import org.example.bookmyshowpaymentservice.payment.api.dto.PaymentResponse;
import org.example.bookmyshowpaymentservice.payment.client.TicketClient;
import org.example.bookmyshowpaymentservice.payment.config.StripeProperties;
import org.example.bookmyshowpaymentservice.payment.model.Payment;
import org.example.bookmyshowpaymentservice.payment.model.PaymentStatus;
import org.example.bookmyshowpaymentservice.payment.repository.PaymentRepository;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.model.StripeObject;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.net.Webhook;
import com.stripe.param.PaymentIntentCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final TicketClient ticketClient;
    private final StripeProperties stripeProperties;

    @Transactional
    public PaymentResponse createPaymentIntent(CreatePaymentRequest request) {
        validateCreateRequest(request);

        // Ensure we only create payments for valid tickets.
        ticketClient.validateTicketExists(request.getTicketId());

        String currency = request.getCurrency().toLowerCase(Locale.ROOT);
        long minorAmount = toMinorUnits(request.getAmount());

        PaymentIntent paymentIntent;
        try {
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(minorAmount)
                     .setCurrency(currency)
                    .addPaymentMethodType("card")
                    .putMetadata("ticketId", String.valueOf(request.getTicketId()))
                    .build();

            paymentIntent = PaymentIntent.create(params);
        } catch (StripeException ex) {
            throw new IllegalStateException("Failed to create payment intent", ex);
        }

        Payment payment = new Payment();
        payment.setTicketId(request.getTicketId());
        payment.setAmount(request.getAmount());
        payment.setCurrency(currency);
        payment.setStatus(PaymentStatus.PENDING);
        payment.setStripePaymentIntentId(paymentIntent.getId());
        payment.setStripeClientSecret(paymentIntent.getClientSecret());

        Payment saved = paymentRepository.save(payment);
        return toResponse(saved);
    }

    @Transactional
    public void handleWebhook(String payload, String signatureHeader) {
        if (payload == null || payload.isBlank()) {
            throw new IllegalArgumentException("Webhook payload is required");
        }
        if (signatureHeader == null || signatureHeader.isBlank()) {
            throw new IllegalArgumentException("Stripe-Signature header is required");
        }
        if (stripeProperties.getWebhookSecret() == null || stripeProperties.getWebhookSecret().isBlank()) {
            throw new IllegalStateException("Stripe webhook secret is not configured");
        }

        Event event;
        try {
            event = Webhook.constructEvent(payload, signatureHeader, stripeProperties.getWebhookSecret());
        } catch (SignatureVerificationException ex) {
            throw new IllegalArgumentException("Invalid Stripe webhook signature", ex);
        } catch (Exception ex) {
            throw new IllegalArgumentException("Invalid Stripe webhook payload", ex);
        }

        switch (event.getType()) {
            case "payment_intent.succeeded" -> updatePaymentStatus(event, PaymentStatus.SUCCESS);
            case "payment_intent.payment_failed" -> updatePaymentStatus(event, PaymentStatus.FAILED);
            case "payment_intent.canceled" -> updatePaymentStatus(event, PaymentStatus.CANCELED);
            default -> log.debug("Ignoring unsupported Stripe event type: {}", event.getType());
        }
    }

    private void updatePaymentStatus(Event event, PaymentStatus status) {
        EventDataObjectDeserializer deserializer = event.getDataObjectDeserializer();
        StripeObject stripeObject = deserializer.getObject().orElseThrow(
                () -> new IllegalArgumentException("Unable to deserialize Stripe event payload")
        );

        if (!(stripeObject instanceof PaymentIntent paymentIntent)) {
            throw new IllegalArgumentException("Expected payment_intent object in Stripe event");
        }

        String paymentIntentId = paymentIntent.getId();

        Payment payment = paymentRepository.findByStripePaymentIntentId(paymentIntentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found for intent id: " + paymentIntentId));

        payment.setStatus(status);
        paymentRepository.save(payment);
    }

    private void validateCreateRequest(CreatePaymentRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Payment request is required");
        }
        if (request.getTicketId() == null || request.getTicketId() <= 0) {
            throw new IllegalArgumentException("Valid ticketId is required");
        }
        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
        if (request.getCurrency() == null || request.getCurrency().isBlank()) {
            throw new IllegalArgumentException("Currency is required");
        }
    }

    private long toMinorUnits(BigDecimal amount) {
        try {
            return amount
                    .setScale(2, RoundingMode.HALF_UP)
                    .movePointRight(2)
                    .longValueExact();
        } catch (ArithmeticException ex) {
            throw new IllegalArgumentException("Invalid amount format", ex);
        }
    }

    private PaymentResponse toResponse(Payment payment) {
        PaymentResponse response = new PaymentResponse();
        response.setPaymentId(payment.getPaymentId());
        response.setTicketId(payment.getTicketId());
        response.setAmount(payment.getAmount());
        response.setCurrency(payment.getCurrency());
        response.setStatus(payment.getStatus());
        response.setStripePaymentIntentId(payment.getStripePaymentIntentId());
        response.setClientSecret(payment.getStripeClientSecret());
        response.setReturnUrl(stripeProperties.getReturnUrl());
        return response;
    }
}







