package io.github.lisalorita.ordermanagement.users.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import io.github.lisalorita.ordermanagement.users.dtos.CreateUserRequest;
import io.github.lisalorita.ordermanagement.users.dtos.CreateUserResponse;
import io.github.lisalorita.ordermanagement.users.entities.User;
import io.github.lisalorita.ordermanagement.users.exceptions.EmailAlreadyExists;
import io.github.lisalorita.ordermanagement.users.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

class UserCreatorTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private PasswordEncoder passwordEncoder;

  @InjectMocks
  private UserCreator userCreator;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("Should create user successfully when email does not exist")
  void shouldCreateUserSuccessfully() {
    CreateUserRequest request = new CreateUserRequest();
    request.setName("Alba");
    request.setEmail("alba@example.com");
    request.setPassword("Password123!");

    UUID generatedId = UUID.randomUUID();
    User savedUser = new User();

    reflectedSetId(savedUser, generatedId);
    savedUser.setName(request.getName());
    savedUser.setEmail(request.getEmail());

    when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
    when(passwordEncoder.encode(request.getPassword())).thenReturn("hashed-password");
    when(userRepository.save(any(User.class))).thenReturn(savedUser);

    CreateUserResponse response = userCreator.run(request);

    assertNotNull(response);
    assertEquals(generatedId, response.getId());
    assertEquals(request.getName(), response.getName());
    assertEquals(request.getEmail(), response.getEmail());

    verify(userRepository, times(1)).existsByEmail(request.getEmail());
    verify(userRepository, times(1)).save(any(User.class));
  }

  @Test
  @DisplayName("Should throw EmailAlreadyExists when email is taken")
  void shouldThrowExceptionWhenEmailExists() {
    CreateUserRequest request = new CreateUserRequest();
    request.setEmail("exists@example.com");

    when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

    assertThrows(EmailAlreadyExists.class, () -> userCreator.run(request));

    verify(userRepository, never()).save(any(User.class));
  }

  private void reflectedSetId(User user, UUID id) {
    try {
      java.lang.reflect.Field field = User.class.getDeclaredField("id");
      field.setAccessible(true);
      field.set(user, id);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}