package io.github.lisalorita.ordermanagement.users.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.lisalorita.ordermanagement.users.dtos.CreateUserRequest;
import io.github.lisalorita.ordermanagement.users.dtos.CreateUserResponse;
import io.github.lisalorita.ordermanagement.users.services.UserCreator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/users")
@Tag(name = "Users", description = "Operations related to user management")
public class UserPostController {

    private final UserCreator creator;

    public UserPostController(UserCreator creator) {
        this.creator = creator;
    }

    @Operation(
        summary = "Create a new user",
        description = "Creates a new user with name, email and password. All fields are validated."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "User created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data (validation failed)"),
        @ApiResponse(responseCode = "409", description = "Email already exists")
    })
    @PostMapping
    public ResponseEntity<CreateUserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {

        CreateUserResponse response = creator.run(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
