package com.reflect.controller;

import com.reflect.config.ReflectProperties;
import com.reflect.controller.dto.BillingStatusResponse;
import com.reflect.controller.dto.CheckoutRequest;
import com.reflect.controller.dto.CheckoutResponse;
import com.reflect.domain.User;
import com.reflect.exception.ApiException;
import com.reflect.repository.UserRepository;
import com.reflect.service.StripeService;
import com.stripe.exception.StripeException;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/billing")
public class BillingController {

    private final StripeService stripeService;
    private final UserRepository userRepository;
    private final Set<String> validPriceIds;
    private final String frontendUrl;

    public BillingController(
            StripeService stripeService,
            UserRepository userRepository,
            ReflectProperties properties
    ) {
        this.stripeService = stripeService;
        this.userRepository = userRepository;
        this.validPriceIds = Set.of(
                properties.stripe().priceIdMonthly(),
                properties.stripe().priceIdAnnual()
        );
        this.frontendUrl = properties.frontendUrl();
    }

    @PostMapping("/checkout")
    public ResponseEntity<CheckoutResponse> createCheckout(
            @AuthenticationPrincipal UUID userId,
            @Valid @RequestBody CheckoutRequest request
    ) {
        if (!validPriceIds.contains(request.priceId())) {
            throw ApiException.badRequest("Invalid price ID");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> ApiException.notFound("User not found"));

        String successUrl = frontendUrl + "/billing/success?session_id={CHECKOUT_SESSION_ID}";
        String cancelUrl = frontendUrl + "/account";

        try {
            String checkoutUrl = stripeService.createCheckoutSession(user, request.priceId(), successUrl, cancelUrl);
            return ResponseEntity.ok(new CheckoutResponse(checkoutUrl));
        } catch (StripeException e) {
            throw ApiException.badRequest("Failed to create checkout session: " + e.getMessage());
        }
    }

    @GetMapping("/status")
    public ResponseEntity<BillingStatusResponse> getStatus(@AuthenticationPrincipal UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> ApiException.notFound("User not found"));

        return ResponseEntity.ok(new BillingStatusResponse(
                user.getSubscriptionStatus(),
                user.isPro()
        ));
    }
}
