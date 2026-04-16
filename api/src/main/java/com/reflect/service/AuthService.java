package com.reflect.service;

import com.reflect.config.JwtProvider;
import com.reflect.config.ReflectProperties;
import com.reflect.controller.dto.AuthResponse;
import com.reflect.controller.dto.LoginRequest;
import com.reflect.controller.dto.RegisterRequest;
import com.reflect.domain.EmailVerificationToken;
import com.reflect.domain.PasswordResetToken;
import com.reflect.domain.RefreshToken;
import com.reflect.domain.User;
import com.reflect.exception.ApiException;
import com.reflect.repository.EmailVerificationTokenRepository;
import com.reflect.repository.PasswordResetTokenRepository;
import com.reflect.repository.RefreshTokenRepository;
import com.reflect.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.OffsetDateTime;
import java.util.HexFormat;
import java.util.UUID;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailVerificationTokenRepository emailVerificationTokenRepository;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final long refreshTokenExpirySeconds;
    private final int resetTokenTtlHours;
    private final int verifyTokenTtlHours;
    private final String frontendUrl;

    public AuthService(
            UserRepository userRepository,
            RefreshTokenRepository refreshTokenRepository,
            PasswordResetTokenRepository passwordResetTokenRepository,
            EmailVerificationTokenRepository emailVerificationTokenRepository,
            JwtProvider jwtProvider,
            PasswordEncoder passwordEncoder,
            EmailService emailService,
            ReflectProperties properties
    ) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.emailVerificationTokenRepository = emailVerificationTokenRepository;
        this.jwtProvider = jwtProvider;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.refreshTokenExpirySeconds = properties.jwt().refreshTokenExpirySeconds();
        this.resetTokenTtlHours = properties.security().resetTokenTtlHours();
        this.verifyTokenTtlHours = properties.security().verifyTokenTtlHours();
        this.frontendUrl = properties.frontendUrl() != null
                ? properties.frontendUrl()
                : "http://localhost:3000";
    }

    public record AuthResult(AuthResponse authResponse, String rawRefreshToken) {}

    @Transactional
    public AuthResult register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw ApiException.conflict("Email already registered");
        }
        String hash = passwordEncoder.encode(request.password());
        User user = new User(request.email(), hash, request.displayName());
        user = userRepository.save(user);
        AuthResult result = createTokens(user);

        // Send verification email — non-blocking, do not fail registration
        try {
            String rawToken = UUID.randomUUID().toString();
            String tokenHash = hashToken(rawToken);
            OffsetDateTime expiresAt = OffsetDateTime.now().plusHours(verifyTokenTtlHours);
            EmailVerificationToken verificationToken = new EmailVerificationToken(user, tokenHash, expiresAt);
            emailVerificationTokenRepository.save(verificationToken);
            emailService.sendVerificationEmail(user.getEmail(), rawToken, frontendUrl);
        } catch (Exception e) {
            log.error("Failed to send verification email for {}: {}", user.getEmail(), e.getMessage());
        }

        return result;
    }

    @Transactional
    public AuthResult login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> ApiException.unauthorized("Invalid email or password"));
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw ApiException.unauthorized("Invalid email or password");
        }
        return createTokens(user);
    }

    @Transactional
    public AuthResult refresh(String rawRefreshToken) {
        String tokenHash = hashToken(rawRefreshToken);
        RefreshToken existing = refreshTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> ApiException.unauthorized("Invalid refresh token"));
        if (existing.isExpired()) {
            refreshTokenRepository.delete(existing);
            throw ApiException.unauthorized("Refresh token expired");
        }
        refreshTokenRepository.delete(existing);
        User user = existing.getUser();
        return createTokens(user);
    }

    @Transactional
    public void logout(String rawRefreshToken) {
        String tokenHash = hashToken(rawRefreshToken);
        refreshTokenRepository.findByTokenHash(tokenHash)
                .ifPresent(refreshTokenRepository::delete);
    }

    @Transactional
    public void forgotPassword(String email) {
        userRepository.findByEmail(email).ifPresent(user -> {
            // Invalidate any existing reset tokens for this user
            passwordResetTokenRepository.deleteAllByUserId(user.getId());

            String rawToken = UUID.randomUUID().toString();
            String tokenHash = hashToken(rawToken);
            OffsetDateTime expiresAt = OffsetDateTime.now().plusHours(resetTokenTtlHours);
            PasswordResetToken resetToken = new PasswordResetToken(user, tokenHash, expiresAt);
            passwordResetTokenRepository.save(resetToken);

            try {
                emailService.sendPasswordResetEmail(user.getEmail(), rawToken, frontendUrl);
            } catch (Exception e) {
                log.error("Failed to send password reset email for {}: {}", user.getEmail(), e.getMessage());
            }
        });
        // Always return silently — no email enumeration
    }

    @Transactional
    public void resetPassword(String rawToken, String newPassword) {
        String tokenHash = hashToken(rawToken);
        PasswordResetToken resetToken = passwordResetTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> ApiException.badRequest("Invalid or expired reset token"));

        if (resetToken.isExpired()) {
            throw ApiException.badRequest("Invalid or expired reset token");
        }
        if (resetToken.isUsed()) {
            throw ApiException.badRequest("Invalid or expired reset token");
        }

        User user = resetToken.getUser();
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        resetToken.markUsed();
        passwordResetTokenRepository.save(resetToken);
    }

    @Transactional
    public void verifyEmail(String rawToken) {
        String tokenHash = hashToken(rawToken);
        EmailVerificationToken verificationToken = emailVerificationTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> ApiException.badRequest("Invalid or expired verification token"));

        if (verificationToken.isExpired()) {
            throw ApiException.badRequest("Invalid or expired verification token");
        }

        User user = verificationToken.getUser();
        user.setEmailVerified(true);
        userRepository.save(user);
        emailVerificationTokenRepository.deleteAllByUserId(user.getId());
    }

    @Transactional
    public void resendVerification(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> ApiException.notFound("User not found"));

        if (user.isEmailVerified()) {
            return; // Already verified, nothing to do
        }

        // Invalidate existing verification tokens
        emailVerificationTokenRepository.deleteAllByUserId(user.getId());

        String rawToken = UUID.randomUUID().toString();
        String tokenHash = hashToken(rawToken);
        OffsetDateTime expiresAt = OffsetDateTime.now().plusHours(verifyTokenTtlHours);
        EmailVerificationToken verificationToken = new EmailVerificationToken(user, tokenHash, expiresAt);
        emailVerificationTokenRepository.save(verificationToken);
        emailService.sendVerificationEmail(user.getEmail(), rawToken, frontendUrl);
    }

    @Transactional
    public void changePassword(UUID userId, String currentPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> ApiException.notFound("User not found"));

        if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
            throw ApiException.unauthorized("Current password is incorrect");
        }

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Transactional
    public User updateProfile(UUID userId, String displayName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> ApiException.notFound("User not found"));

        user.setDisplayName(displayName);
        return userRepository.save(user);
    }

    private AuthResult createTokens(User user) {
        String accessToken = jwtProvider.generateAccessToken(user.getId(), user.getEmail());
        long expiresIn = jwtProvider.getAccessTokenExpirySeconds();
        String rawRefreshToken = UUID.randomUUID().toString();
        String refreshHash = hashToken(rawRefreshToken);
        OffsetDateTime expiresAt = OffsetDateTime.now().plusSeconds(refreshTokenExpirySeconds);
        RefreshToken refreshToken = new RefreshToken(user, refreshHash, expiresAt);
        refreshTokenRepository.save(refreshToken);
        return new AuthResult(new AuthResponse(accessToken, expiresIn), rawRefreshToken);
    }

    static String hashToken(String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}
