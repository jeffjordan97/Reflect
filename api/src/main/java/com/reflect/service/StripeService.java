package com.reflect.service;

import com.reflect.config.ReflectProperties;
import com.reflect.domain.User;
import com.reflect.repository.UserRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.Subscription;
import com.stripe.model.checkout.Session;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StripeService {

    private static final Logger log = LoggerFactory.getLogger(StripeService.class);

    private final String webhookSecret;
    private final UserRepository userRepository;

    public StripeService(ReflectProperties properties, UserRepository userRepository) {
        Stripe.apiKey = properties.stripe().secretKey();
        this.webhookSecret = properties.stripe().webhookSecret();
        this.userRepository = userRepository;
    }

    public String getWebhookSecret() {
        return webhookSecret;
    }

    /**
     * Creates a Stripe Checkout Session for subscription purchase.
     * If the user has no Stripe Customer ID, creates one first and persists it.
     *
     * @return the checkout session URL for redirect
     */
    @Transactional
    public String createCheckoutSession(User user, String priceId, String successUrl, String cancelUrl)
            throws StripeException {

        if (user.getStripeCustomerId() == null) {
            Customer customer = Customer.create(
                    CustomerCreateParams.builder()
                            .setEmail(user.getEmail())
                            .putMetadata("userId", user.getId().toString())
                            .build()
            );
            user.setStripeCustomerId(customer.getId());
            userRepository.save(user);
        }

        Session session = Session.create(
                SessionCreateParams.builder()
                        .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                        .setCustomer(user.getStripeCustomerId())
                        .setSuccessUrl(successUrl)
                        .setCancelUrl(cancelUrl)
                        .addLineItem(
                                SessionCreateParams.LineItem.builder()
                                        .setPrice(priceId)
                                        .setQuantity(1L)
                                        .build()
                        )
                        .build()
        );

        return session.getUrl();
    }

    /**
     * Handles a completed checkout session by activating the user's subscription.
     */
    @Transactional
    public void handleCheckoutCompleted(Session session) {
        String customerId = session.getCustomer();
        userRepository.findByStripeCustomerId(customerId).ifPresentOrElse(
                user -> {
                    user.setSubscriptionStatus("ACTIVE");
                    userRepository.save(user);
                    log.info("Subscription activated for user {} via checkout", user.getId());
                },
                () -> log.warn("Checkout completed for unknown Stripe customer: {}", customerId)
        );
    }

    /**
     * Handles subscription status changes from Stripe webhooks.
     * Maps Stripe statuses to internal statuses: active -> ACTIVE, canceled/unpaid -> CANCELED, past_due -> PAST_DUE.
     */
    @Transactional
    public void handleSubscriptionUpdated(Subscription subscription) {
        String customerId = subscription.getCustomer();
        userRepository.findByStripeCustomerId(customerId).ifPresentOrElse(
                user -> {
                    String newStatus = mapStripeStatus(subscription.getStatus());
                    user.setSubscriptionStatus(newStatus);
                    userRepository.save(user);
                    log.info("Subscription updated for user {}: {}", user.getId(), newStatus);
                },
                () -> log.warn("Subscription updated for unknown Stripe customer: {}", customerId)
        );
    }

    /**
     * Handles subscription deletion by reverting the user to FREE tier.
     */
    @Transactional
    public void handleSubscriptionDeleted(Subscription subscription) {
        String customerId = subscription.getCustomer();
        userRepository.findByStripeCustomerId(customerId).ifPresentOrElse(
                user -> {
                    user.setSubscriptionStatus("FREE");
                    userRepository.save(user);
                    log.info("Subscription deleted for user {}, reverted to FREE", user.getId());
                },
                () -> log.warn("Subscription deleted for unknown Stripe customer: {}", customerId)
        );
    }

    static String mapStripeStatus(String stripeStatus) {
        return switch (stripeStatus) {
            case "active" -> "ACTIVE";
            case "canceled", "unpaid" -> "CANCELED";
            case "past_due" -> "PAST_DUE";
            default -> "FREE";
        };
    }
}
