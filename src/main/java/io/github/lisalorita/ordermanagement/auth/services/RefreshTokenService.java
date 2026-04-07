package io.github.lisalorita.ordermanagement.auth.services;

import io.github.lisalorita.ordermanagement.auth.dtos.TokenRefreshResponse;
import io.github.lisalorita.ordermanagement.auth.entities.RefreshToken;
import io.github.lisalorita.ordermanagement.auth.exceptions.RefreshTokenException;
import io.github.lisalorita.ordermanagement.auth.infrastructure.JwtTokenProvider;
import io.github.lisalorita.ordermanagement.auth.repositories.RefreshTokenRepository;
import io.github.lisalorita.ordermanagement.users.entities.User;
import io.github.lisalorita.ordermanagement.users.repositories.UserRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {

    @Value("${app.jwt.refresh-expiration-ms}")
    private Long refreshTokenDurationMs;

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public RefreshTokenService(
            RefreshTokenRepository refreshTokenRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenProvider jwtTokenProvider) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public Optional<RefreshToken> findByIdentifier(String identifier) {
        return refreshTokenRepository.findByIdentifier(identifier);
    }


    @Transactional
    public String createRefreshToken(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));

        // Use findByUser to check if a token already exists and reuse it
        RefreshToken refreshToken = refreshTokenRepository.findByUser(user)
                .orElseGet(() -> {
                    RefreshToken newToken = new RefreshToken();
                    newToken.setUser(user);
                    return newToken;
                });

        String identifier = UUID.randomUUID().toString();
        String secret = UUID.randomUUID().toString();

        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
        refreshToken.setIdentifier(identifier);
        refreshToken.setToken(passwordEncoder.encode(secret));

        refreshTokenRepository.save(refreshToken);
        return identifier + ":" + secret;
    }

    @Transactional
    public TokenRefreshResponse refresh(String requestRefreshToken) {
        String[] parts = requestRefreshToken.split(":");
        if (parts.length != 2) {
            throw new RefreshTokenException(requestRefreshToken, "Invalid refresh token format");
        }

        String identifier = parts[0];
        String secret = parts[1];

        RefreshToken refreshToken = refreshTokenRepository.findByIdentifier(identifier)
                .orElseThrow(() -> new RefreshTokenException(identifier, "Refresh token not found"));

        verifyExpiration(refreshToken);
        verifySecret(refreshToken, secret);

        String userEmail = refreshToken.getUser().getEmail();

        // Rotation: Invalidate the current one and issue a new one
        refreshTokenRepository.delete(refreshToken);
        String newRefreshToken = createRefreshToken(userEmail);

        String accessToken = jwtTokenProvider.generateToken(userEmail);
        return new TokenRefreshResponse(accessToken, newRefreshToken);
    }

    public void verifySecret(RefreshToken refreshToken, String secret) {
        if (!passwordEncoder.matches(secret, refreshToken.getToken())) {
            throw new RefreshTokenException(refreshToken.getIdentifier(), "Invalid refresh token secret");
        }
    }


    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(token);
            throw new RefreshTokenException(token.getIdentifier(), "Refresh token was expired. Please make a new signin request");
        }

        return token;
    }


    @Transactional
    public void deleteToken(RefreshToken token) {
        refreshTokenRepository.delete(token);
    }

    @Transactional
    public int deleteByUserId(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));
        return refreshTokenRepository.deleteByUser(user);
    }
}
