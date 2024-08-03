package grwm.develop.auth.login.application.kakao.dto;

import jakarta.annotation.Nullable;

public record KakaoAccount(@Nullable String email, Profile profile) {
}
