package grwm.develop.auth.login.application.kakao.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import grwm.develop.auth.login.application.MemberProfile;
import grwm.develop.member.Member;
import grwm.develop.member.Service;

@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public record KakaoMemberProfile(Long id, KakaoAccount kakaoAccount) implements MemberProfile {

    @Override
    public Member toEntity() {
        return Member.builder()
                .name(kakaoAccount.profile().nickname())
                .email(kakaoAccount.email())
                .service(Service.KAKAO)
                .build();
    }
}