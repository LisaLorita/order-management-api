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
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.github.lisalorita.ordermanagement.shared.api.ApiErrorResponse;

@RestController
@RequestMapping("/users")
@Tag(name = "Users", description = "Operations related to user management")
public class UserGetController {

  private final UserFinder finder;

  public UserGetController(UserFinder finder) {
    this.finder = finder;
  }

  @Operation(summary = "Get user by ID", description = "Finds a user by their unique UUID. Returns 404 if the user does not exist.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "User found successfully"),
      @ApiResponse(responseCode = "400", description = "Invalid UUID format", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
      @ApiResponse(responseCode = "404", description = "User not found", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
  })

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
