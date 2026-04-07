package io.github.lisalorita.ordermanagement.auth.services;

import io.github.lisalorita.ordermanagement.auth.dtos.TokenRefreshResponse;
import io.github.lisalorita.ordermanagement.auth.entities.RefreshToken;
import io.github.lisalorita.ordermanagement.auth.exceptions.RefreshTokenException;
import io.github.lisalorita.ordermanagement.auth.infrastructure.JwtTokenProvider;
import io.github.lisalorita.ordermanagement.auth.repositories.RefreshTokenRepository;
import io.github.lisalorita.ordermanagement.users.entities.User;
import io.github.lisalorita.ordermanagement.users.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(refreshTokenService, "refreshTokenDurationMs", 3600000L);
    }

    @Test
    @DisplayName("createRefreshToken should save even and return combined string")
    void shouldCreateRefreshToken() {
        String email = "test@example.com";
        User user = new User();
        user.setEmail(email);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(any())).thenReturn("hashed-secret");

        String result = refreshTokenService.createRefreshToken(email);

        assertNotNull(result);
        assertTrue(result.contains(":"));
        verify(refreshTokenRepository, times(1)).save(any(RefreshToken.class));
    }

    @Test
    @DisplayName("verifySecret should pass for valid secret")
    void shouldVerifySecretSuccessfully() {
        RefreshToken token = new RefreshToken();
        token.setToken("hashed-secret");
        String rawSecret = "raw-secret";

        when(passwordEncoder.matches(rawSecret, "hashed-secret")).thenReturn(true);

        assertDoesNotThrow(() -> refreshTokenService.verifySecret(token, rawSecret));
    }

    @Test
    @DisplayName("verifySecret should throw for invalid secret")
    void shouldThrowForInvalidSecret() {
        RefreshToken token = new RefreshToken();
        token.setToken("hashed-secret");
        token.setIdentifier("id-123");
        String rawSecret = "wrong-secret";

        when(passwordEncoder.matches(rawSecret, "hashed-secret")).thenReturn(false);

        assertThrows(RefreshTokenException.class, () -> refreshTokenService.verifySecret(token, rawSecret));
    }

    @Test
    @DisplayName("verifyExpiration should throw when expired")
    void shouldThrowWhenTokenIsExpired() {
        RefreshToken token = new RefreshToken();
        token.setExpiryDate(Instant.now().minusSeconds(10));
        token.setToken("some-token");

        assertThrows(RefreshTokenException.class, () -> refreshTokenService.verifyExpiration(token));
        verify(refreshTokenRepository, times(1)).delete(token);
    }

    @Test
    @DisplayName("verifyExpiration should return token when valid")
    void shouldReturnTokenWhenNotExpired() {
        RefreshToken token = new RefreshToken();
        token.setExpiryDate(Instant.now().plusSeconds(3600));

        RefreshToken result = refreshTokenService.verifyExpiration(token);

        assertEquals(token, result);
        verify(refreshTokenRepository, never()).delete(any());
    }

    @Test
    @DisplayName("deleteToken should call repository delete")
    void shouldDeleteToken() {
        RefreshToken token = new RefreshToken();
        refreshTokenService.deleteToken(token);
        verify(refreshTokenRepository, times(1)).delete(token);
    }

    @Test
    @DisplayName("refresh should return new tokens for valid request")
    void shouldRefreshSuccessfully() {
        String combinedToken = "id:secret";
        RefreshToken token = new RefreshToken();
        User user = new User();
        user.setEmail("user@test.com");
        token.setUser(user);
        token.setIdentifier("id");
        token.setToken("hashed-secret");
        token.setExpiryDate(Instant.now().plusSeconds(3600));

        when(refreshTokenRepository.findByIdentifier("id")).thenReturn(Optional.of(token));
        when(passwordEncoder.matches("secret", "hashed-secret")).thenReturn(true);
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(any())).thenReturn("new-hashed-secret");
        when(jwtTokenProvider.generateToken("user@test.com")).thenReturn("new-access-token");

        TokenRefreshResponse response = refreshTokenService.refresh(combinedToken);

        assertNotNull(response);
        assertEquals("new-access-token", response.getAccessToken());
        verify(refreshTokenRepository).delete(token);
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }
}
