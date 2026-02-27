package org.example.bookmyshowpaymentservice.payment.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "payment")
@Getter
@Setter
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long paymentId;

    @Column(name = "ticket_id", nullable = false)
    private Long ticketId;

    @Column(name = "amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(name = "currency", nullable = false, length = 10)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private PaymentStatus status;

    @Column(name = "stripe_payment_intent_id", nullable = false, unique = true, length = 100)
    private String stripePaymentIntentId;

    @Column(name = "stripe_client_secret", length = 255)
    private String stripeClientSecret;
}
