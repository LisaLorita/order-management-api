package io.github.lisalorita.ordermanagement.users.services;

import java.util.UUID;

import org.springframework.stereotype.Service;

import io.github.lisalorita.ordermanagement.users.entities.User;
import io.github.lisalorita.ordermanagement.users.exceptions.UserNotFound;
import io.github.lisalorita.ordermanagement.users.repositories.UserRepository;

@Service
public class UserFinder {

  private final UserRepository userRepository;

  public UserFinder(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public User run(UUID id) {
    return this.userRepository.findById(id)
        .orElseThrow(() -> new UserNotFound(id));
  }

}
