package io.github.lisalorita.ordermanagement.users.controllers;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import io.github.lisalorita.ordermanagement.shared.api.GlobalExceptionHandler;
import io.github.lisalorita.ordermanagement.users.exceptions.UserNotFound;
import io.github.lisalorita.ordermanagement.users.services.UserDeleter;

import org.springframework.test.context.bean.override.mockito.MockitoBean;

@WebMvcTest(UserDeleteController.class)
@Import(GlobalExceptionHandler.class)
class UserDeleteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserDeleter userDeleter;

    @Test
    @DisplayName("DELETE /users/{id} should return 204 No Content")
    void shouldDeleteUser() throws Exception {
        UUID id = UUID.randomUUID();

        doNothing().when(userDeleter).run(id);

        mockMvc.perform(delete("/users/{id}", id))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /users/{id} should return 404 Not Found when user does not exist")
    void shouldReturn404WhenNotFound() throws Exception {
        UUID id = UUID.randomUUID();

        doThrow(new UserNotFound(id)).when(userDeleter).run(id);

        mockMvc.perform(delete("/users/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("USER_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("The user with id " + id + " was not found."));
    }

    @Test
    @DisplayName("DELETE /users/{id} should return 400 Bad Request when ID is invalid")
    void shouldReturn400WhenInvalidId() throws Exception {
        mockMvc.perform(delete("/users/{id}", "invalid-uuid"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_FORMAT"))
                .andExpect(jsonPath("$.message").value("Invalid format for parameter: id"));
    }
}
