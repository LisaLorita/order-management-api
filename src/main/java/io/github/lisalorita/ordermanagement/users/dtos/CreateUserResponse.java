package io.github.lisalorita.ordermanagement.users.dtos;

import java.util.UUID;

public class CreateUserResponse {
  private UUID id;
  private String name;
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
