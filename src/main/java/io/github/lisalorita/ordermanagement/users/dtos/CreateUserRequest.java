package io.github.lisalorita.ordermanagement.users.dtos;

import io.github.lisalorita.ordermanagement.shared.validation.StrongPassword;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Schema(description = "Request to create a new user")
public class CreateUserRequest {

  @Schema(description = "User name", example = "John Doe")
  @NotBlank(message = "Name is required")
  @Pattern(regexp = "^[\\p{L} ]+$", message = "Name must contain only letters")
  private String name;

  @Schema(description = "User email")
  @NotBlank(message = "Email is required")
  @Email(message = "Email is not valid")
  private String email;

  @Schema(description = "User password", example = "P@ssw0rd!")
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