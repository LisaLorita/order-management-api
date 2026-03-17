package io.github.lisalorita.ordermanagement.users.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.lisalorita.ordermanagement.shared.api.GlobalExceptionHandler;
import io.github.lisalorita.ordermanagement.users.dtos.CreateUserRequest;
import io.github.lisalorita.ordermanagement.users.dtos.CreateUserResponse;
import io.github.lisalorita.ordermanagement.users.services.UserCreator;

import org.springframework.test.context.bean.override.mockito.MockitoBean;

@WebMvcTest(UserPostController.class)
@Import(GlobalExceptionHandler.class)
class UserPostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @MockitoBean
    private UserCreator userCreator;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        this.objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("POST /users should return 201 Created and user data")
    void shouldCreateUser() throws Exception {
        CreateUserRequest request = new CreateUserRequest();
        request.setName("Alba");
        request.setEmail("alba@example.com");
        request.setPassword("Password123!");

        UUID id = UUID.randomUUID();
        CreateUserResponse response = new CreateUserResponse(id, "Alba", "alba@example.com");

        when(userCreator.run(any(CreateUserRequest.class))).thenReturn(response);

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value("Alba"))
                .andExpect(jsonPath("$.email").value("alba@example.com"));
    }

    @Test
    @DisplayName("POST /users should return 400 Bad Request if validation fails")
    void shouldReturn400WhenInvalidRequest() throws Exception {
        CreateUserRequest request = new CreateUserRequest();
        request.setEmail("not-an-email");

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }
}
