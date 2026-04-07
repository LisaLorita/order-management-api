package io.github.lisalorita.ordermanagement.auth.services;

import io.github.lisalorita.ordermanagement.auth.entities.RefreshToken;
import io.github.lisalorita.ordermanagement.auth.exceptions.RefreshTokenException;
import io.github.lisalorita.ordermanagement.auth.repositories.RefreshTokenRepository;
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

    public RefreshTokenService(
            RefreshTokenRepository refreshTokenRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<RefreshToken> findByIdentifier(String identifier) {
        return refreshTokenRepository.findByIdentifier(identifier);
    }


    @Transactional
    public String createRefreshToken(String email) {
        RefreshToken refreshToken = new RefreshToken();
        String identifier = UUID.randomUUID().toString();
        String secret = UUID.randomUUID().toString();

        refreshToken.setUser(userRepository.findByEmail(email).get());
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
        refreshToken.setIdentifier(identifier);
        refreshToken.setToken(passwordEncoder.encode(secret));

        refreshTokenRepository.save(refreshToken);
        return identifier + ":" + secret;
    }

    public void verifySecret(RefreshToken refreshToken, String secret) {
        if (!passwordEncoder.matches(secret, refreshToken.getToken())) {
            throw new RefreshTokenException(refreshToken.getIdentifier(), "Invalid refresh token secret");
        }
    }


    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new RefreshTokenException(token.getToken(), "Refresh token was expired. Please make a new signin request");
        }

        return token;
    }


    @Transactional
    public void deleteToken(RefreshToken token) {
        refreshTokenRepository.delete(token);
    }

    @Transactional
    public int deleteByUserId(UUID userId) {

        return refreshTokenRepository.deleteByUser(userRepository.findById(userId).get());
    }
}
