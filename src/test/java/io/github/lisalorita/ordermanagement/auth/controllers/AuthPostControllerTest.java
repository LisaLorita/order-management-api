package io.github.lisalorita.ordermanagement.auth.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Optional;

import io.github.lisalorita.ordermanagement.auth.dtos.LoginRequest;
import io.github.lisalorita.ordermanagement.auth.dtos.LoginResponse;
import io.github.lisalorita.ordermanagement.auth.services.UserAuthenticator;
import io.github.lisalorita.ordermanagement.auth.exceptions.InvalidCredentialsException;
import io.github.lisalorita.ordermanagement.auth.exceptions.RefreshTokenException;
import io.github.lisalorita.ordermanagement.auth.services.RefreshTokenService;
import io.github.lisalorita.ordermanagement.shared.api.GlobalExceptionHandler;
import io.github.lisalorita.ordermanagement.shared.api.SecurityConfig;
import io.github.lisalorita.ordermanagement.auth.entities.RefreshToken;
import io.github.lisalorita.ordermanagement.auth.dtos.TokenRefreshRequest;
import io.github.lisalorita.ordermanagement.auth.dtos.TokenRefreshResponse;

import org.springframework.test.context.bean.override.mockito.MockitoBean;
import io.github.lisalorita.ordermanagement.auth.infrastructure.JwtAuthenticationFilter;
import io.github.lisalorita.ordermanagement.auth.infrastructure.JwtTokenProvider;
import io.github.lisalorita.ordermanagement.users.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;

@WebMvcTest(AuthPostController.class)
@Import(GlobalExceptionHandler.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthPostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserAuthenticator userAuthenticator;

    @MockitoBean
    private RefreshTokenService refreshTokenService;


    @MockitoBean
    private JwtAuthenticationFilter jwtAuthFilter;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("POST /auth/login should return 200 and both tokens")
    void shouldLoginSuccessfully() throws Exception {
        LoginRequest request = new LoginRequest("test@example.com", "Password123!");
        LoginResponse response = new LoginResponse("mocked-jwt-token", "mocked-refresh-token");

        when(userAuthenticator.run(argThat((LoginRequest req) -> 
            "test@example.com".equals(req.getEmail()) && "Password123!".equals(req.getPassword())
        ))).thenReturn(response);


        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mocked-jwt-token"))
                .andExpect(jsonPath("$.refreshToken").value("mocked-refresh-token"));
    }


    @Test
    @DisplayName("POST /auth/login should return 400 for invalid data")
    void shouldReturn400ForInvalidData() throws Exception {
        LoginRequest request = new LoginRequest("not-an-email", "");

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /auth/login should return 401 for invalid credentials")
    void shouldReturn401ForInvalidCredentials() throws Exception {
        LoginRequest request = new LoginRequest("user@example.com", "wrong-password");

        when(userAuthenticator.run(any(LoginRequest.class)))
                .thenThrow(new InvalidCredentialsException());

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("INVALID_CREDENTIALS"))
                .andExpect(jsonPath("$.message").value("Invalid credentials"));
    }

    @Test
    @DisplayName("POST /auth/refresh should return 200 and new tokens")
    void shouldRefreshTokenSuccessfully() throws Exception {
        String combinedToken = "identifier:secret";
        TokenRefreshRequest request = new TokenRefreshRequest();
        request.setRefreshToken(combinedToken);

        TokenRefreshResponse response = new TokenRefreshResponse("new-access-token", "new-identifier:new-secret");

        when(refreshTokenService.refresh(combinedToken)).thenReturn(response);

        mockMvc.perform(post("/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("new-access-token"))
                .andExpect(jsonPath("$.refreshToken").value("new-identifier:new-secret"));
    }

    @Test
    @DisplayName("POST /auth/refresh should return 400 for malformed token")
    void shouldReturn400ForMalformedToken() throws Exception {
        TokenRefreshRequest request = new TokenRefreshRequest();
        request.setRefreshToken("malformed-token"); // no colon

        when(refreshTokenService.refresh("malformed-token"))
                .thenThrow(new RefreshTokenException("malformed-token", "Invalid refresh token format"));

        mockMvc.perform(post("/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("REFRESH_TOKEN_ERROR"));
    }
}

