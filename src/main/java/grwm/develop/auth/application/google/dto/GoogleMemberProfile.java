package grwm.develop.auth.application.google.dto;

import grwm.develop.auth.application.MemberProfile;
import grwm.develop.member.Member;
import grwm.develop.member.Service;

public record GoogleMemberProfile(String name, String email) implements MemberProfile {

    @Override
    public Member toEntity() {
        return Member.builder()
                .name(name)
                .email(email)
                .service(Service.GOOGLE)
                .build();
    }
}
