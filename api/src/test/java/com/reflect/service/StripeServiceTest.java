package com.reflect.service;

import com.reflect.domain.User;
import com.reflect.repository.UserRepository;
import com.stripe.model.Subscription;
import com.stripe.model.checkout.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StripeServiceTest {

    @Mock private UserRepository userRepository;

    // ── Status Mapping ──────────────────────────────────────────────────

    @Test
    void mapStripeStatus_mapsActiveToActive() {
        assertEquals("ACTIVE", StripeService.mapStripeStatus("active"));
    }

    @Test
    void mapStripeStatus_mapsCanceledToCanceled() {
        assertEquals("CANCELED", StripeService.mapStripeStatus("canceled"));
    }

    @Test
    void mapStripeStatus_mapsUnpaidToCanceled() {
        assertEquals("CANCELED", StripeService.mapStripeStatus("unpaid"));
    }

    @Test
    void mapStripeStatus_mapsPastDueToPastDue() {
        assertEquals("PAST_DUE", StripeService.mapStripeStatus("past_due"));
    }

    @Test
    void mapStripeStatus_mapsUnknownToFree() {
        assertEquals("FREE", StripeService.mapStripeStatus("trialing"));
        assertEquals("FREE", StripeService.mapStripeStatus("incomplete"));
    }

    // ── Checkout Completed ──────────────────────────────────────────────

    @Test
    void handleCheckoutCompleted_activatesSubscription() {
        User user = new User("test@example.com", "hash", "Test");
        assertEquals("FREE", user.getSubscriptionStatus());

        when(userRepository.findByStripeCustomerId("cus_123")).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        Session session = mock(Session.class);
        when(session.getCustomer()).thenReturn("cus_123");

        // Create service with mocked userRepository (no real Stripe API key needed for these methods)
        StripeService service = createTestService();
        service.handleCheckoutCompleted(session);

        assertEquals("ACTIVE", user.getSubscriptionStatus());
        verify(userRepository).save(user);
    }

    @Test
    void handleCheckoutCompleted_logsWarningForUnknownCustomer() {
        when(userRepository.findByStripeCustomerId("cus_unknown")).thenReturn(Optional.empty());

        Session session = mock(Session.class);
        when(session.getCustomer()).thenReturn("cus_unknown");

        StripeService service = createTestService();
        // Should not throw — just logs a warning
        assertDoesNotThrow(() -> service.handleCheckoutCompleted(session));
        verify(userRepository, never()).save(any());
    }

    // ── Subscription Updated ────────────────────────────────────────────

    @Test
    void handleSubscriptionUpdated_updatesStatusToActive() {
        User user = new User("test@example.com", "hash", "Test");
        when(userRepository.findByStripeCustomerId("cus_123")).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        Subscription subscription = mock(Subscription.class);
        when(subscription.getCustomer()).thenReturn("cus_123");
        when(subscription.getStatus()).thenReturn("active");

        StripeService service = createTestService();
        service.handleSubscriptionUpdated(subscription);

        assertEquals("ACTIVE", user.getSubscriptionStatus());
        verify(userRepository).save(user);
    }

    @Test
    void handleSubscriptionUpdated_updatesStatusToPastDue() {
        User user = new User("test@example.com", "hash", "Test");
        when(userRepository.findByStripeCustomerId("cus_123")).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        Subscription subscription = mock(Subscription.class);
        when(subscription.getCustomer()).thenReturn("cus_123");
        when(subscription.getStatus()).thenReturn("past_due");

        StripeService service = createTestService();
        service.handleSubscriptionUpdated(subscription);

        assertEquals("PAST_DUE", user.getSubscriptionStatus());
    }

    @Test
    void handleSubscriptionUpdated_updatesStatusToCanceled() {
        User user = new User("test@example.com", "hash", "Test");
        when(userRepository.findByStripeCustomerId("cus_123")).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        Subscription subscription = mock(Subscription.class);
        when(subscription.getCustomer()).thenReturn("cus_123");
        when(subscription.getStatus()).thenReturn("canceled");

        StripeService service = createTestService();
        service.handleSubscriptionUpdated(subscription);

        assertEquals("CANCELED", user.getSubscriptionStatus());
    }

    // ── Subscription Deleted ────────────────────────────────────────────

    @Test
    void handleSubscriptionDeleted_revertsToFree() {
        User user = new User("test@example.com", "hash", "Test");
        user.setSubscriptionStatus("ACTIVE");

        when(userRepository.findByStripeCustomerId("cus_123")).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        Subscription subscription = mock(Subscription.class);
        when(subscription.getCustomer()).thenReturn("cus_123");

        StripeService service = createTestService();
        service.handleSubscriptionDeleted(subscription);

        assertEquals("FREE", user.getSubscriptionStatus());
        verify(userRepository).save(user);
    }

    @Test
    void handleSubscriptionDeleted_logsWarningForUnknownCustomer() {
        when(userRepository.findByStripeCustomerId("cus_unknown")).thenReturn(Optional.empty());

        Subscription subscription = mock(Subscription.class);
        when(subscription.getCustomer()).thenReturn("cus_unknown");

        StripeService service = createTestService();
        assertDoesNotThrow(() -> service.handleSubscriptionDeleted(subscription));
        verify(userRepository, never()).save(any());
    }

    // ── User isPro ──────────────────────────────────────────────────────

    @Test
    void user_isProOnlyWhenActive() {
        User user = new User("test@example.com", "hash", "Test");
        assertFalse(user.isPro()); // FREE by default

        user.setSubscriptionStatus("ACTIVE");
        assertTrue(user.isPro());

        user.setSubscriptionStatus("CANCELED");
        assertFalse(user.isPro());

        user.setSubscriptionStatus("PAST_DUE");
        assertFalse(user.isPro());
    }

    /**
     * Creates a StripeService for testing webhook handler methods.
     * The Stripe API key is set to a dummy value since we only test methods
     * that operate on already-deserialized Stripe objects (no real API calls).
     */
    private StripeService createTestService() {
        com.reflect.config.ReflectProperties.Stripe stripeProps =
                new com.reflect.config.ReflectProperties.Stripe(
                        "sk_test_dummy", "whsec_test_dummy", "price_monthly", "price_annual"
                );
        com.reflect.config.ReflectProperties props =
                new com.reflect.config.ReflectProperties(
                        null, null, stripeProps, null, null, null, null, null, null, null, null
                );
        return new StripeService(props, userRepository);
    }
}
