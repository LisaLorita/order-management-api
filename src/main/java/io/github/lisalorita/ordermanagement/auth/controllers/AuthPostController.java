package io.github.lisalorita.ordermanagement.auth.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.lisalorita.ordermanagement.auth.dtos.LoginRequest;
import io.github.lisalorita.ordermanagement.auth.dtos.LoginResponse;
import io.github.lisalorita.ordermanagement.auth.dtos.TokenRefreshRequest;
import io.github.lisalorita.ordermanagement.auth.dtos.TokenRefreshResponse;
import io.github.lisalorita.ordermanagement.auth.entities.RefreshToken;
import io.github.lisalorita.ordermanagement.auth.exceptions.RefreshTokenException;
import io.github.lisalorita.ordermanagement.auth.infrastructure.JwtTokenProvider;

import io.github.lisalorita.ordermanagement.auth.services.RefreshTokenService;
import io.github.lisalorita.ordermanagement.auth.services.UserAuthenticator;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Operations related to authentication")
public class AuthPostController {

    private final UserAuthenticator authenticator;
    private final RefreshTokenService refreshTokenService;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthPostController(UserAuthenticator authenticator, RefreshTokenService refreshTokenService, JwtTokenProvider jwtTokenProvider) {
        this.authenticator = authenticator;
        this.refreshTokenService = refreshTokenService;
        this.jwtTokenProvider = jwtTokenProvider;
    }


    @Operation(summary = "Login", description = "Authenticates a user and returns a JWT token and a refresh token")
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authenticator.run(request));
    }

    @Operation(summary = "Refresh Token", description = "Obtains a new access token using a valid refresh token and rotates it")
    @PostMapping("/refresh")
    public ResponseEntity<TokenRefreshResponse> refreshToken(@Valid @RequestBody TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();
        String[] parts = requestRefreshToken.split(":");
        if (parts.length != 2) {
            throw new RefreshTokenException(requestRefreshToken, "Invalid refresh token format");
        }

        String identifier = parts[0];
        String secret = parts[1];

        return refreshTokenService.findByIdentifier(identifier)
                .map(refreshTokenService::verifyExpiration)
                .map(token -> {
                    refreshTokenService.verifySecret(token, secret);
                    String userEmail = token.getUser().getEmail();

                    // Rotation: Invalidate the current one and issue a new one
                    refreshTokenService.deleteToken(token);
                    String newRefreshToken = refreshTokenService.createRefreshToken(userEmail);

                    String accessToken = jwtTokenProvider.generateToken(userEmail);
                    return ResponseEntity.ok(new TokenRefreshResponse(accessToken, newRefreshToken));
                })
                .orElseThrow(() -> new RefreshTokenException(identifier, "Refresh token not found"));
    }


}
