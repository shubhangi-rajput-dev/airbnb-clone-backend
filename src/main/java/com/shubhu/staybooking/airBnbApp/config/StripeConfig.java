package com.shubhu.staybooking.airBnbApp.config;

import com.stripe.Stripe;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class that initializes Stripe SDK configuration.
 * Sets the Stripe API key from application properties.
 */
@Configuration
public class StripeConfig {

    /**
     * Constructor that receives the Stripe secret key from configuration
     * and configures the Stripe SDK globally.
     *
     * @param stripeSecretKey the Stripe secret API key used for authentication
     */
    public StripeConfig(@Value("${stripe.secret.key}") String stripeSecretKey) {
        Stripe.apiKey = stripeSecretKey;
    }
}
