package com.sidework.payment.application.config;

import io.portone.sdk.server.payment.PaymentClient;
import io.portone.sdk.server.webhook.WebhookVerifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PortOneConfig {
    @Bean
    public PaymentClient paymentClient(PortOneSecretProperties secret) {
        return new PaymentClient(secret.api(), "https://api.portone.io", null);
    }

    @Bean
    public WebhookVerifier webhookVerifier(PortOneSecretProperties secret) {
        return new WebhookVerifier(secret.webhook());
    }
}
