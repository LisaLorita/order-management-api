package io.github.lisalorita.ordermanagement.users.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.lisalorita.ordermanagement.users.dto.CreateUserRequest;
import io.github.lisalorita.ordermanagement.users.dto.CreateUserResponse;
import io.github.lisalorita.ordermanagement.users.service.UserCreator;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/users")
public class UserPostController {

    private final UserCreator creator;

    public UserPostController(UserCreator creator) {
        this.creator = creator;
    }

    @PostMapping
    public ResponseEntity<CreateUserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {

        CreateUserResponse response = creator.run(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
