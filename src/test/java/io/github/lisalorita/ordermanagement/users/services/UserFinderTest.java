package io.github.lisalorita.ordermanagement.users.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import io.github.lisalorita.ordermanagement.users.entities.User;
import io.github.lisalorita.ordermanagement.users.exceptions.UserNotFound;
import io.github.lisalorita.ordermanagement.users.repositories.UserRepository;

class UserFinderTest {

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private UserFinder userFinder;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("Should return user when ID exists")
  void shouldReturnUserWhenIdExists() {
    UUID id = UUID.randomUUID();
    User user = new User();
    reflectedSetId(user, id);
    user.setName("Alba");
    user.setEmail("alba@example.com");

    when(userRepository.findById(id)).thenReturn(Optional.of(user));

    User result = userFinder.run(id);

    assertNotNull(result);
    assertEquals(id, result.getId());
    assertEquals("Alba", result.getName());
    assertEquals("alba@example.com", result.getEmail());
    verify(userRepository, times(1)).findById(id);
  }

  @Test
  @DisplayName("Should throw UserNotFound when ID does not exist")
  void shouldThrowExceptionWhenIdDoesNotExist() {
    UUID id = UUID.randomUUID();
    when(userRepository.findById(id)).thenReturn(Optional.empty());

    assertThrows(UserNotFound.class, () -> userFinder.run(id));
    verify(userRepository, times(1)).findById(id);
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
