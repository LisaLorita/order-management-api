package io.github.lisalorita.ordermanagement.users.services;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import io.github.lisalorita.ordermanagement.users.exceptions.UserNotFound;
import io.github.lisalorita.ordermanagement.users.repositories.UserRepository;

class UserDeleterTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDeleter userDeleter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should delete user when user exists")
    void shouldDeleteUserWhenExists() {
        UUID id = UUID.randomUUID();

        when(userRepository.existsById(id)).thenReturn(true);

        userDeleter.run(id);

        verify(userRepository, times(1)).existsById(id);
        verify(userRepository, times(1)).deleteById(id);
    }

    @Test
    @DisplayName("Should throw UserNotFound when user does not exist")
    void shouldThrowExceptionWhenNotFound() {
        UUID id = UUID.randomUUID();

        when(userRepository.existsById(id)).thenReturn(false);

        assertThrows(UserNotFound.class, () -> userDeleter.run(id));

        verify(userRepository, times(1)).existsById(id);
        verify(userRepository, never()).deleteById(id);
    }
}
