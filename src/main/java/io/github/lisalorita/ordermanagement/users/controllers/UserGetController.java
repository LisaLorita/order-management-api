package io.github.lisalorita.ordermanagement.users.controllers;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.lisalorita.ordermanagement.users.dtos.FindUserResponse;
import io.github.lisalorita.ordermanagement.users.entities.User;
import io.github.lisalorita.ordermanagement.users.services.UserFinder;

@RestController
@RequestMapping("/users")
public class UserGetController {

  private final UserFinder finder;

  public UserGetController(UserFinder finder) {
    this.finder = finder;
  }

  @GetMapping("/{id}")
  public ResponseEntity<FindUserResponse> getUserById(@PathVariable UUID id) {
    User user = finder.run(id);

    return ResponseEntity.ok(
        new FindUserResponse(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getCreatedAt()));
  }

}
