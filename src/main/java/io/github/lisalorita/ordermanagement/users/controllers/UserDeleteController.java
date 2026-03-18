package io.github.lisalorita.ordermanagement.users.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.lisalorita.ordermanagement.users.services.UserDeleter;

import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserDeleteController {

  private final UserDeleter deleter;

  public UserDeleteController(UserDeleter deleter) {
    this.deleter = deleter;
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
    deleter.run(id);
    return ResponseEntity.noContent().build();
  }

}
