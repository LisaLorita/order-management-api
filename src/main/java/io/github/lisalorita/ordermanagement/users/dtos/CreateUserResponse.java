package io.github.lisalorita.ordermanagement.users.dtos;

import java.time.LocalDateTime;
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

  @Schema(description = "User creation date")
  private LocalDateTime createdAt;

  public CreateUserResponse(UUID id, String name, String email, LocalDateTime createdAt) {
    this.id = id;
    this.name = name;
    this.email = email;
    this.createdAt = createdAt;
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

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }
}
