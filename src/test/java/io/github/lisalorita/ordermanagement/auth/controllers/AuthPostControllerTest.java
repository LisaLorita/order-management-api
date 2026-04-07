package io.github.lisalorita.ordermanagement.auth.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.when;
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

import io.github.lisalorita.ordermanagement.auth.dtos.LoginRequest;
import io.github.lisalorita.ordermanagement.auth.dtos.LoginResponse;
import io.github.lisalorita.ordermanagement.auth.services.UserAuthenticator;
import io.github.lisalorita.ordermanagement.auth.exceptions.InvalidCredentialsException;
import io.github.lisalorita.ordermanagement.shared.api.GlobalExceptionHandler;
import io.github.lisalorita.ordermanagement.shared.api.SecurityConfig;
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
    private JwtAuthenticationFilter jwtAuthFilter;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("POST /auth/login should return 200 and token")
    void shouldLoginSuccessfully() throws Exception {
        LoginRequest request = new LoginRequest("test@example.com", "Password123!");
        LoginResponse response = new LoginResponse("mocked-jwt-token");

        // Use specific request matchers to avoid matching invalid data
        when(userAuthenticator.run(argThat(req -> 
            "test@example.com".equals(req.getEmail()) && "Password123!".equals(req.getPassword())
        ))).thenReturn(response);

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mocked-jwt-token"));
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
}
