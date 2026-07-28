package org.example.bookmyshowpaymentservice.payment.config;

import com.stripe.Stripe;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class StripeConfig {

    private final StripeProperties stripeProperties;

    @PostConstruct
    void init() {
        String secretKey = stripeProperties.getSecretKey();
        if (secretKey == null || secretKey.isBlank()) {
            log.warn("Stripe secret key is missing. Falling back to mock dev key 'sk_test_mock_dev_key'.");
            secretKey = "sk_test_mock_dev_key";
        }

        Stripe.apiKey = secretKey;

        if (!secretKey.startsWith("sk_") && !secretKey.startsWith("rk_")) {
            log.warn("Stripe key format looks unusual. Expected key to start with sk_ or rk_");
        }
    }
}

