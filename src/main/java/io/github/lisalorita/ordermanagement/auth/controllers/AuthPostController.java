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
        return ResponseEntity.ok(refreshTokenService.refresh(request.getRefreshToken()));
    }


}
