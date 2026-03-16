package io.github.lisalorita.ordermanagement.users.services;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import io.github.lisalorita.ordermanagement.users.dtos.CreateUserRequest;
import io.github.lisalorita.ordermanagement.users.dtos.CreateUserResponse;
import io.github.lisalorita.ordermanagement.users.entities.User;
import io.github.lisalorita.ordermanagement.users.repositories.UserRepository;

@Service
public class UserCreator {

    private final UserRepository userRepository;

    public UserCreator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public CreateUserResponse run(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Email already in use");
        }
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());

        User savedUser = userRepository.save(user);

        return new CreateUserResponse(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail());
    }
}
