package io.github.lisalorita.ordermanagement.users.controllers;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import io.github.lisalorita.ordermanagement.shared.api.GlobalExceptionHandler;
import io.github.lisalorita.ordermanagement.users.entities.User;
import io.github.lisalorita.ordermanagement.users.exceptions.UserNotFound;
import io.github.lisalorita.ordermanagement.users.services.UserFinder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import io.github.lisalorita.ordermanagement.auth.infrastructure.JwtAuthenticationFilter;
import io.github.lisalorita.ordermanagement.auth.infrastructure.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

@WebMvcTest(UserGetController.class)
@Import(GlobalExceptionHandler.class)
@AutoConfigureMockMvc(addFilters = false)
class UserGetControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private UserFinder userFinder;

  @MockitoBean
  private JwtAuthenticationFilter jwtAuthFilter;

  @MockitoBean
  private JwtTokenProvider jwtTokenProvider;

  @MockitoBean
  private AuthenticationProvider authenticationProvider;

  @MockitoBean
  private UserDetailsService userDetailsService;

  @MockitoBean
  private PasswordEncoder passwordEncoder;

  @Test
  @DisplayName("GET /users/{id} should return 200 and user data")
  void shouldReturnUser() throws Exception {
    UUID id = UUID.randomUUID();
    User user = new User();
    reflectedSetId(user, id);
    user.setName("Alba");
    user.setEmail("alba@example.com");

    reflectedSetField(user, "createdAt", LocalDateTime.now());

    when(userFinder.run(id)).thenReturn(user);

    mockMvc.perform(get("/users/" + id))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(id.toString()))
        .andExpect(jsonPath("$.name").value("Alba"))
        .andExpect(jsonPath("$.email").value("alba@example.com"))
        .andExpect(jsonPath("$.createdAt").exists());
  }

  @Test
  @DisplayName("GET /users/{id} should return 404 if user not found")
  void shouldReturn404WhenNotFound() throws Exception {
    UUID id = UUID.randomUUID();
    when(userFinder.run(id)).thenThrow(new UserNotFound(id));

    mockMvc.perform(get("/users/" + id))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("USER_NOT_FOUND"));
  }

  @Test
  @DisplayName("GET /users/{id} should return 400 if UUID is invalid")
  void shouldReturn400WhenInvalidUuid() throws Exception {
    mockMvc.perform(get("/users/invalid-uuid"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("INVALID_FORMAT"));
  }

  private void reflectedSetId(User user, UUID id) {
    reflectedSetField(user, "id", id);
  }

  private void reflectedSetField(Object target, String fieldName, Object value) {
    try {
      java.lang.reflect.Field field = target.getClass().getDeclaredField(fieldName);
      field.setAccessible(true);
      field.set(target, value);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
