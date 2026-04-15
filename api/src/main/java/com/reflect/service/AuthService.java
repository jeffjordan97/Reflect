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

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;
    private final long refreshTokenExpirySeconds;

    public AuthService(
            UserRepository userRepository,
            RefreshTokenRepository refreshTokenRepository,
            JwtProvider jwtProvider,
            PasswordEncoder passwordEncoder,
            ReflectProperties.Jwt jwtProperties
    ) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtProvider = jwtProvider;
        this.passwordEncoder = passwordEncoder;
        this.refreshTokenExpirySeconds = jwtProperties.refreshTokenExpirySeconds();
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
        return createTokens(user);
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
