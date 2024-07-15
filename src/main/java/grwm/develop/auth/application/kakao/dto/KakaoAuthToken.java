package grwm.develop.auth.application.kakao.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import grwm.develop.auth.application.AuthToken;

@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public record KakaoAuthToken(
        String tokenType,
        String accessToken,
        Integer expiresIn,
        String refreshToken,
        Integer refreshTokenExpiresIn,
        String scope) implements AuthToken {
}
