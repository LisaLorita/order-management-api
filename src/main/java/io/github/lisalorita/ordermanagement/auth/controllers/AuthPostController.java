package io.github.lisalorita.ordermanagement.auth.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.lisalorita.ordermanagement.auth.dtos.LoginRequest;
import io.github.lisalorita.ordermanagement.auth.dtos.LoginResponse;
import io.github.lisalorita.ordermanagement.auth.services.UserAuthenticator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Operations related to authentication")
public class AuthPostController {

    private final UserAuthenticator authenticator;

    public AuthPostController(UserAuthenticator authenticator) {
        this.authenticator = authenticator;
    }

    @Operation(summary = "Login", description = "Authenticates a user and returns a JWT token")
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authenticator.run(request));
    }
}
