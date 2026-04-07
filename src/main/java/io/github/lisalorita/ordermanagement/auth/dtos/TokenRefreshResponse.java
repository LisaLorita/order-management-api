package io.github.lisalorita.ordermanagement.auth.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response containing a new access token and fresh refresh token")
public class TokenRefreshResponse {
    @Schema(description = "New access token")
    private String accessToken;

    @Schema(description = "Fresh refresh token")
    private String refreshToken;

    @Schema(description = "Token type (Bearer)")
    private String tokenType = "Bearer";

    public TokenRefreshResponse(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }
}
