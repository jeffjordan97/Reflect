package com.reflect.controller;

import com.reflect.config.ReflectProperties;
import com.reflect.controller.dto.CheckoutRequest;
import com.reflect.domain.User;
import com.reflect.exception.ApiException;
import com.reflect.repository.UserRepository;
import com.reflect.service.StripeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BillingControllerTest {

    @Mock private StripeService stripeService;
    @Mock private UserRepository userRepository;

    private BillingController billingController;
    private UUID userId;
    private User user;

    @BeforeEach
    void setUp() {
        ReflectProperties.Stripe stripeProps = new ReflectProperties.Stripe(
                "sk_test", "whsec_test", "price_monthly", "price_annual"
        );
        ReflectProperties props = new ReflectProperties(
                null, null, stripeProps, null, null, null, null, null, null, null, "http://localhost:3000"
        );
        billingController = new BillingController(stripeService, userRepository, props);
        userId = UUID.randomUUID();
        user = new User("test@example.com", "hash", "Test User");
    }

    @Test
    void createCheckout_rejectsInvalidPriceId() {
        var request = new CheckoutRequest("price_invalid");

        ApiException ex = assertThrows(ApiException.class,
                () -> billingController.createCheckout(userId, request));
        assertEquals(400, ex.getStatus().value());
        assertTrue(ex.getMessage().contains("Invalid price ID"));
    }

    @Test
    void createCheckout_returnsCheckoutUrl() throws Exception {
        var request = new CheckoutRequest("price_monthly");
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(stripeService.createCheckoutSession(eq(user), eq("price_monthly"), anyString(), anyString()))
                .thenReturn("https://checkout.stripe.com/session123");

        var response = billingController.createCheckout(userId, request);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("https://checkout.stripe.com/session123", response.getBody().url());
    }

    @Test
    void createCheckout_acceptsAnnualPriceId() throws Exception {
        var request = new CheckoutRequest("price_annual");
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(stripeService.createCheckoutSession(eq(user), eq("price_annual"), anyString(), anyString()))
                .thenReturn("https://checkout.stripe.com/annual123");

        var response = billingController.createCheckout(userId, request);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("https://checkout.stripe.com/annual123", response.getBody().url());
    }

    @Test
    void getStatus_returnsFreeByDefault() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        var response = billingController.getStatus(userId);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("FREE", response.getBody().status());
        assertFalse(response.getBody().pro());
    }

    @Test
    void getStatus_returnsActiveForProUser() {
        user.setSubscriptionStatus("ACTIVE");
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        var response = billingController.getStatus(userId);

        assertEquals("ACTIVE", response.getBody().status());
        assertTrue(response.getBody().pro());
    }

    @Test
    void getStatus_throwsNotFoundForMissingUser() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        ApiException ex = assertThrows(ApiException.class,
                () -> billingController.getStatus(userId));
        assertEquals(404, ex.getStatus().value());
    }

    @Test
    void createCheckout_throwsNotFoundForMissingUser() {
        var request = new CheckoutRequest("price_monthly");
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        ApiException ex = assertThrows(ApiException.class,
                () -> billingController.createCheckout(userId, request));
        assertEquals(404, ex.getStatus().value());
    }
}
