package org.example.bookmyshowpaymentservice.payment.api;

import org.example.bookmyshowpaymentservice.common.dto.ApiResponse;
import org.example.bookmyshowpaymentservice.payment.api.dto.CreatePaymentRequest;
import org.example.bookmyshowpaymentservice.payment.api.dto.PaymentResponse;
import org.example.bookmyshowpaymentservice.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/payments/intent")
    public ResponseEntity<ApiResponse<PaymentResponse>> createPaymentIntent(@Valid @RequestBody CreatePaymentRequest request) {
        PaymentResponse response = paymentService.createPaymentIntent(request);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        200,
                        "Payment intent created successfully",
                        response,
                        LocalDateTime.now()
                )
        );
    }

    @PostMapping("/payments/webhook")
    public ResponseEntity<Void> handleWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String signatureHeader
    ) {
        paymentService.handleWebhook(payload, signatureHeader);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/payments/return")
    public ResponseEntity<ApiResponse<String>> paymentReturn() {
        return ResponseEntity.ok(
                new ApiResponse<>(
                        200,
                        "Payment redirect handled",
                        "Return URL reached",
                        LocalDateTime.now()
                )
        );
    }
}

