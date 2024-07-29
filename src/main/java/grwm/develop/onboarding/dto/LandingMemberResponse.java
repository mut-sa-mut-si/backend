package grwm.develop.onboarding.dto;

import grwm.develop.member.Member;

public record LandingMemberResponse(MemberDTO member) {

    public static LandingMemberResponse from(Member member) {
        return new LandingMemberResponse(
                new MemberDTO(
                        member.getId(),
                        member.getName()));
    }

    public record MemberDTO(Long id, String name) {
    }
}
