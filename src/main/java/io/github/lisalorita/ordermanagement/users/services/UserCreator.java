package io.github.lisalorita.ordermanagement.users.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.lisalorita.ordermanagement.users.dtos.CreateUserRequest;
import io.github.lisalorita.ordermanagement.users.dtos.CreateUserResponse;
import io.github.lisalorita.ordermanagement.users.entities.User;
import io.github.lisalorita.ordermanagement.users.repositories.UserRepository;
import io.github.lisalorita.ordermanagement.users.exceptions.EmailAlreadyExists;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class UserCreator {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserCreator(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public CreateUserResponse run(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExists(request.getEmail());
        }
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        User savedUser = userRepository.save(user);

        return new CreateUserResponse(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getCreatedAt());
    }
}
