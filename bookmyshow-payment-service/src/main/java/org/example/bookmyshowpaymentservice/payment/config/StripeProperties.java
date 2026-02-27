package org.example.bookmyshowpaymentservice.payment.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "payment.stripe")
@Getter
@Setter
public class StripeProperties {
    private String secretKey;
    private String webhookSecret;
    private String returnUrl;
}

