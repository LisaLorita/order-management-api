package io.github.lisalorita.ordermanagement.users.dtos;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response after creating a new user")
public class CreateUserResponse {

  @Schema(description = "User ID")
  private UUID id;

  @Schema(description = "User name")
  private String name;

  @Schema(description = "User email")
  private String email;

  public CreateUserResponse(UUID id, String name, String email) {
    this.id = id;
    this.name = name;
    this.email = email;
  }

  public UUID getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getEmail() {
    return email;
  }
}
