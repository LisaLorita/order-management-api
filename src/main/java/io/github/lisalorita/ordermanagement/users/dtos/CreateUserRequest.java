package io.github.lisalorita.ordermanagement.users.dtos;

import io.github.lisalorita.ordermanagement.shared.validation.StrongPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class CreateUserRequest {

  @NotBlank(message = "Name is required")
  @Pattern(regexp = "^[\\p{L} ]+$", message = "Name must contain only letters")
  private String name;

  @NotBlank(message = "Email is required")
  @Email(message = "Email is not valid")
  private String email;

  @NotBlank(message = "Password is required")
  @StrongPassword(minLength = 8, minLowercase = 1, minUppercase = 1, minNumbers = 1, minSymbols = 1)
  private String password;

  public CreateUserRequest() {
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }
}