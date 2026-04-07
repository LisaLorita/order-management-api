package io.github.lisalorita.ordermanagement.auth.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request to obtain a new access token using a refresh token")
public class TokenRefreshRequest {
    @NotBlank
    @Schema(description = "Refresh Token")
    private String refreshToken;

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
