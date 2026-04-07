package io.github.lisalorita.ordermanagement.auth.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response containing the JWT token after successful login")
public class LoginResponse {

    @Schema(description = "JWT Token")
    private final String token;

    @Schema(description = "Refresh Token")
    private final String refreshToken;

    public LoginResponse(String token, String refreshToken) {
        this.token = token;
        this.refreshToken = refreshToken;
    }

    public String getToken() {
        return token;
    }

    public String getRefreshToken() {
        return refreshToken;
    }
}

