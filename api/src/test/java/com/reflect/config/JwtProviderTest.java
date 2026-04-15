package com.reflect.config;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Base64;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class JwtProviderTest {

    private static JwtProvider jwtProvider;

    @BeforeAll
    static void setUp() throws Exception {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        KeyPair keyPair = keyGen.generateKeyPair();

        String privateKeyPem = "-----BEGIN PRIVATE KEY-----\n"
                + Base64.getMimeEncoder(64, "\n".getBytes()).encodeToString(keyPair.getPrivate().getEncoded())
                + "\n-----END PRIVATE KEY-----";
        String publicKeyPem = "-----BEGIN PUBLIC KEY-----\n"
                + Base64.getMimeEncoder(64, "\n".getBytes()).encodeToString(keyPair.getPublic().getEncoded())
                + "\n-----END PUBLIC KEY-----";

        ReflectProperties.Jwt jwtProps = new ReflectProperties.Jwt(
                privateKeyPem, publicKeyPem, 3600, 86400
        );
        jwtProvider = new JwtProvider(jwtProps);
    }

    @Test
    void generateToken_containsSubjectClaim() {
        UUID userId = UUID.randomUUID();
        String token = jwtProvider.generateAccessToken(userId, "test@example.com");
        assertNotNull(token);
        assertTrue(jwtProvider.validateToken(token));
        assertEquals(userId, jwtProvider.getUserIdFromToken(token));
    }

    @Test
    void validateToken_returnsFalseForTamperedToken() {
        UUID userId = UUID.randomUUID();
        String token = jwtProvider.generateAccessToken(userId, "test@example.com");
        String tampered = token.substring(0, token.length() - 5) + "XXXXX";
        assertFalse(jwtProvider.validateToken(tampered));
    }

    @Test
    void validateToken_returnsFalseForGarbage() {
        assertFalse(jwtProvider.validateToken("not.a.jwt"));
    }

    @Test
    void getUserIdFromToken_returnsCorrectId() {
        UUID userId = UUID.randomUUID();
        String token = jwtProvider.generateAccessToken(userId, "user@example.com");
        assertEquals(userId, jwtProvider.getUserIdFromToken(token));
    }
}
