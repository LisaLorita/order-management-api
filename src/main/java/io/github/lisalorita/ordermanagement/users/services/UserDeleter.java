package io.github.lisalorita.ordermanagement.users.services;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.lisalorita.ordermanagement.users.exceptions.UserNotFound;
import io.github.lisalorita.ordermanagement.users.repositories.UserRepository;

@Service
public class UserDeleter {

  private final UserRepository userRepository;

  public UserDeleter(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Transactional
  public void run(UUID id) {
    if (!this.userRepository.existsById(id)) {
      throw new UserNotFound(id);
    }
    this.userRepository.deleteById(id);
  }
}
