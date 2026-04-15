package com.reflect.service;

import com.reflect.config.JwtProvider;
import com.reflect.config.ReflectProperties;
import com.reflect.controller.dto.AuthResponse;
import com.reflect.controller.dto.LoginRequest;
import com.reflect.controller.dto.RegisterRequest;
import com.reflect.domain.RefreshToken;
import com.reflect.domain.User;
import com.reflect.exception.ApiException;
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

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private RefreshTokenRepository refreshTokenRepository;
    @Mock private JwtProvider jwtProvider;

    private AuthService authService;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(4);

    @BeforeEach
    void setUp() {
        ReflectProperties.Jwt jwtProps = new ReflectProperties.Jwt("", "", 3600, 604800);
        authService = new AuthService(
                userRepository, refreshTokenRepository,
                jwtProvider, passwordEncoder, jwtProps
        );
    }

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
    void register_throwsConflictForDuplicateEmail() {
        var request = new RegisterRequest("exists@example.com", "password123", "User");
        when(userRepository.existsByEmail("exists@example.com")).thenReturn(true);

        ApiException ex = assertThrows(ApiException.class, () -> authService.register(request));
        assertEquals(409, ex.getStatus().value());
    }

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
}
