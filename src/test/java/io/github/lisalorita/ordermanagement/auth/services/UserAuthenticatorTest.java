package io.github.lisalorita.ordermanagement.auth.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import io.github.lisalorita.ordermanagement.auth.dtos.LoginRequest;
import io.github.lisalorita.ordermanagement.auth.dtos.LoginResponse;
import io.github.lisalorita.ordermanagement.auth.infrastructure.JwtTokenProvider;
import io.github.lisalorita.ordermanagement.users.entities.User;
import io.github.lisalorita.ordermanagement.users.repositories.UserRepository;

class UserAuthenticatorTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private UserAuthenticator userAuthenticator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should authenticate successfully and return token")
    void shouldAuthenticateSuccessfully() {
        String email = "test@example.com";
        String password = "password";
        String token = "jwt-token";

        User user = new User();
        user.setEmail(email);
        user.setPassword("hashed-password");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, user.getPassword())).thenReturn(true);
        when(jwtTokenProvider.generateToken(email)).thenReturn(token);

        LoginRequest request = new LoginRequest(email, password);
        LoginResponse response = userAuthenticator.run(request);

        assertNotNull(response);
        assertEquals(token, response.getToken());
        verify(userRepository).findByEmail(email);
        verify(jwtTokenProvider).generateToken(email);
    }

    @Test
    @DisplayName("Should throw exception for invalid credentials")
    void shouldThrowExceptionForInvalidCredentials() {
        String email = "test@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        LoginRequest request = new LoginRequest(email, "pw");
        assertThrows(RuntimeException.class, () -> userAuthenticator.run(request));
    }
}
