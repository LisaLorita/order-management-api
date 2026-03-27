package io.github.lisalorita.ordermanagement.auth.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response containing the JWT token after successful login")
public class LoginResponse {

    @Schema(description = "JWT Token")
    private final String token;

    public LoginResponse(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
