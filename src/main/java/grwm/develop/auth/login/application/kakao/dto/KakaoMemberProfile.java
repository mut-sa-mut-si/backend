package grwm.develop.auth.login.application.kakao.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import grwm.develop.auth.login.application.MemberProfile;
import grwm.develop.member.Member;

@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public record KakaoMemberProfile(Long id, KakaoAccount kakaoAccount) implements MemberProfile {

    @Override
    public Member toEntity() {
        if (kakaoAccount.email().isEmpty()) {
            return Member.builder()
                    .name(kakaoAccount.profile().nickname())
                    .email(id.toString() + "@grwm.com")
                    .point(1000)
                    .build();
        } else {
            return Member.builder()
                    .name(kakaoAccount.profile().nickname())
                    .email(kakaoAccount.email())
                    .point(1000)
                    .build();
        }
    }
}