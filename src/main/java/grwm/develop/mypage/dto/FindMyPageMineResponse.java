package grwm.develop.mypage.dto;

import grwm.develop.member.Member;

public record FindMyPageMineResponse(MemberDTO member) {

    public static FindMyPageMineResponse of(Member member) {
        return new FindMyPageMineResponse(new MemberDTO(member.getId(), member.getName(), member.getEmail(),
                member.getPoint()));
    }

    public record MemberDTO(Long id, String name, String email, int point) {
    }
}
