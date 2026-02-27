package org.example.bookmyshowpaymentservice.payment.api.dto;

import org.example.bookmyshowpaymentservice.payment.model.PaymentStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class PaymentResponse {

    private Long paymentId;
    private Long ticketId;
    private BigDecimal amount;
    private String currency;
    private PaymentStatus status;
    private String stripePaymentIntentId;
    private String clientSecret;
    private String returnUrl;
}

