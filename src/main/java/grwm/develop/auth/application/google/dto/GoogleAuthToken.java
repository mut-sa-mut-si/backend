package grwm.develop.auth.application.google.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import grwm.develop.auth.application.AuthToken;

@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public record GoogleAuthToken(
        String tokenType,
        String accessToken,
        Integer expiresIn,
        String refreshToken,
        String scope) implements AuthToken {
}
