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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private RefreshTokenRepository refreshTokenRepository;
    @Mock private PasswordResetTokenRepository passwordResetTokenRepository;
    @Mock private EmailVerificationTokenRepository emailVerificationTokenRepository;
    @Mock private JwtProvider jwtProvider;
    @Mock private EmailService emailService;

    private AuthService authService;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(4);

    @BeforeEach
    void setUp() {
        ReflectProperties.Jwt jwtProps = new ReflectProperties.Jwt("", "", 3600, 604800);
        ReflectProperties.Security secProps = new ReflectProperties.Security(4, 5, 15, 24, 48, 90);
        ReflectProperties.Resend resendProps = new ReflectProperties.Resend("test-key", "test@reflect.app");
        ReflectProperties props = new ReflectProperties(
                jwtProps, null, null, null, null, null, null, null, secProps, resendProps, "http://localhost:3000"
        );
        authService = new AuthService(
                userRepository, refreshTokenRepository,
                passwordResetTokenRepository, emailVerificationTokenRepository,
                jwtProvider, passwordEncoder, emailService, props
        );
    }

    // ── Registration ─────────────────────────────────────────────────────

    @Test
    void register_createsUserAndReturnsTokens() {
        var request = new RegisterRequest("test@example.com", "password123", "Test User");
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(jwtProvider.generateAccessToken(any(), eq("test@example.com"))).thenReturn("access-token");
        when(jwtProvider.getAccessTokenExpirySeconds()).thenReturn(3600L);

        AuthService.AuthResult result = authService.register(request);

        assertNotNull(result);
        assertEquals("access-token", result.authResponse().accessToken());
        assertEquals(3600, result.authResponse().expiresIn());
        assertNotNull(result.rawRefreshToken());

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertEquals("test@example.com", userCaptor.getValue().getEmail());
        assertTrue(passwordEncoder.matches("password123", userCaptor.getValue().getPasswordHash()));
    }

    @Test
    void register_sendsVerificationEmail() {
        var request = new RegisterRequest("verify@example.com", "password123", "Test User");
        when(userRepository.existsByEmail("verify@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(jwtProvider.generateAccessToken(any(), eq("verify@example.com"))).thenReturn("access-token");
        when(jwtProvider.getAccessTokenExpirySeconds()).thenReturn(3600L);

        authService.register(request);

        verify(emailVerificationTokenRepository).save(any(EmailVerificationToken.class));
        verify(emailService).sendVerificationEmail(eq("verify@example.com"), anyString(), eq("http://localhost:3000"));
    }

    @Test
    void register_throwsConflictForDuplicateEmail() {
        var request = new RegisterRequest("exists@example.com", "password123", "User");
        when(userRepository.existsByEmail("exists@example.com")).thenReturn(true);

        ApiException ex = assertThrows(ApiException.class, () -> authService.register(request));
        assertEquals(409, ex.getStatus().value());
    }

    // ── Login ────────────────────────────────────────────────────────────

    @Test
    void login_returnsTokensForValidCredentials() {
        var request = new LoginRequest("test@example.com", "password123");
        User user = new User("test@example.com", passwordEncoder.encode("password123"), "Test");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(jwtProvider.generateAccessToken(any(), eq("test@example.com"))).thenReturn("access-token");
        when(jwtProvider.getAccessTokenExpirySeconds()).thenReturn(3600L);

        AuthService.AuthResult result = authService.login(request);

        assertNotNull(result);
        assertEquals("access-token", result.authResponse().accessToken());
    }

    @Test
    void login_throwsUnauthorizedForWrongPassword() {
        var request = new LoginRequest("test@example.com", "wrong-password");
        User user = new User("test@example.com", passwordEncoder.encode("correct-password"), "Test");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        ApiException ex = assertThrows(ApiException.class, () -> authService.login(request));
        assertEquals(401, ex.getStatus().value());
    }

    @Test
    void login_throwsUnauthorizedForNonexistentEmail() {
        var request = new LoginRequest("ghost@example.com", "password");
        when(userRepository.findByEmail("ghost@example.com")).thenReturn(Optional.empty());

        ApiException ex = assertThrows(ApiException.class, () -> authService.login(request));
        assertEquals(401, ex.getStatus().value());
    }

    // ── Forgot Password ──────────────────────────────────────────────────

    @Test
    void forgotPassword_sendsEmailForExistingUser() {
        User user = new User("user@example.com", "hash", "User");
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        authService.forgotPassword("user@example.com");

        verify(passwordResetTokenRepository).deleteAllByUserId(any());
        verify(passwordResetTokenRepository).save(any(PasswordResetToken.class));
        verify(emailService).sendPasswordResetEmail(eq("user@example.com"), anyString(), eq("http://localhost:3000"));
    }

    @Test
    void forgotPassword_silentlySucceedsForUnknownEmail() {
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        assertDoesNotThrow(() -> authService.forgotPassword("unknown@example.com"));

        verify(passwordResetTokenRepository, never()).save(any());
        verify(emailService, never()).sendPasswordResetEmail(anyString(), anyString(), anyString());
    }

    // ── Reset Password ───────────────────────────────────────────────────

    @Test
    void resetPassword_updatesPasswordForValidToken() {
        String rawToken = UUID.randomUUID().toString();
        String tokenHash = AuthService.hashToken(rawToken);
        User user = new User("user@example.com", passwordEncoder.encode("old-password"), "User");
        PasswordResetToken resetToken = new PasswordResetToken(
                user, tokenHash, OffsetDateTime.now().plusHours(24)
        );
        when(passwordResetTokenRepository.findByTokenHash(tokenHash)).thenReturn(Optional.of(resetToken));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        authService.resetPassword(rawToken, "new-password123");

        verify(userRepository).save(any(User.class));
        verify(passwordResetTokenRepository).save(any(PasswordResetToken.class));
    }

    @Test
    void resetPassword_throwsBadRequestForInvalidToken() {
        String rawToken = UUID.randomUUID().toString();
        String tokenHash = AuthService.hashToken(rawToken);
        when(passwordResetTokenRepository.findByTokenHash(tokenHash)).thenReturn(Optional.empty());

        ApiException ex = assertThrows(ApiException.class, () -> authService.resetPassword(rawToken, "new-pass123"));
        assertEquals(400, ex.getStatus().value());
    }

    @Test
    void resetPassword_throwsBadRequestForExpiredToken() {
        String rawToken = UUID.randomUUID().toString();
        String tokenHash = AuthService.hashToken(rawToken);
        User user = new User("user@example.com", "hash", "User");
        PasswordResetToken resetToken = new PasswordResetToken(
                user, tokenHash, OffsetDateTime.now().minusHours(1)
        );
        when(passwordResetTokenRepository.findByTokenHash(tokenHash)).thenReturn(Optional.of(resetToken));

        ApiException ex = assertThrows(ApiException.class, () -> authService.resetPassword(rawToken, "new-pass123"));
        assertEquals(400, ex.getStatus().value());
    }

    // ── Email Verification ───────────────────────────────────────────────

    @Test
    void verifyEmail_setsEmailVerifiedForValidToken() {
        String rawToken = UUID.randomUUID().toString();
        String tokenHash = AuthService.hashToken(rawToken);
        User user = new User("user@example.com", "hash", "User");
        EmailVerificationToken verificationToken = new EmailVerificationToken(
                user, tokenHash, OffsetDateTime.now().plusHours(48)
        );
        when(emailVerificationTokenRepository.findByTokenHash(tokenHash)).thenReturn(Optional.of(verificationToken));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        authService.verifyEmail(rawToken);

        assertTrue(user.isEmailVerified());
        verify(userRepository).save(user);
        verify(emailVerificationTokenRepository).deleteAllByUserId(any());
    }

    @Test
    void verifyEmail_throwsBadRequestForExpiredToken() {
        String rawToken = UUID.randomUUID().toString();
        String tokenHash = AuthService.hashToken(rawToken);
        User user = new User("user@example.com", "hash", "User");
        EmailVerificationToken verificationToken = new EmailVerificationToken(
                user, tokenHash, OffsetDateTime.now().minusHours(1)
        );
        when(emailVerificationTokenRepository.findByTokenHash(tokenHash)).thenReturn(Optional.of(verificationToken));

        ApiException ex = assertThrows(ApiException.class, () -> authService.verifyEmail(rawToken));
        assertEquals(400, ex.getStatus().value());
    }

    @Test
    void verifyEmail_throwsBadRequestForInvalidToken() {
        String rawToken = UUID.randomUUID().toString();
        String tokenHash = AuthService.hashToken(rawToken);
        when(emailVerificationTokenRepository.findByTokenHash(tokenHash)).thenReturn(Optional.empty());

        ApiException ex = assertThrows(ApiException.class, () -> authService.verifyEmail(rawToken));
        assertEquals(400, ex.getStatus().value());
    }

    // ── Resend Verification ──────────────────────────────────────────────

    @Test
    void resendVerification_sendsNewToken() {
        UUID userId = UUID.randomUUID();
        User user = new User("user@example.com", "hash", "User");
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        authService.resendVerification(userId);

        verify(emailVerificationTokenRepository).deleteAllByUserId(any());
        verify(emailVerificationTokenRepository).save(any(EmailVerificationToken.class));
        verify(emailService).sendVerificationEmail(eq("user@example.com"), anyString(), eq("http://localhost:3000"));
    }

    @Test
    void resendVerification_skipsIfAlreadyVerified() {
        UUID userId = UUID.randomUUID();
        User user = new User("user@example.com", "hash", "User");
        user.setEmailVerified(true);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        authService.resendVerification(userId);

        verify(emailVerificationTokenRepository, never()).save(any());
        verify(emailService, never()).sendVerificationEmail(anyString(), anyString(), anyString());
    }

    // ── Change Password ──────────────────────────────────────────────────

    @Test
    void changePassword_updatesPasswordForCorrectCurrent() {
        UUID userId = UUID.randomUUID();
        User user = new User("user@example.com", passwordEncoder.encode("current-pass"), "User");
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        authService.changePassword(userId, "current-pass", "new-password123");

        verify(userRepository).save(any(User.class));
    }

    @Test
    void changePassword_throwsUnauthorizedForWrongCurrent() {
        UUID userId = UUID.randomUUID();
        User user = new User("user@example.com", passwordEncoder.encode("correct-pass"), "User");
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        ApiException ex = assertThrows(ApiException.class,
                () -> authService.changePassword(userId, "wrong-pass", "new-password123"));
        assertEquals(401, ex.getStatus().value());
    }

    // ── Update Profile ───────────────────────────────────────────────────

    @Test
    void updateProfile_updatesDisplayName() {
        UUID userId = UUID.randomUUID();
        User user = new User("user@example.com", "hash", "Old Name");
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User updated = authService.updateProfile(userId, "New Name");

        assertEquals("New Name", updated.getDisplayName());
        verify(userRepository).save(user);
    }
}
