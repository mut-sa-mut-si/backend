package grwm.develop.mypage.dto;

import grwm.develop.member.Member;

public record FindMyPageResponse(boolean isMe, boolean isSubscribed, MemberDTO member) {

    public static FindMyPageResponse of(boolean isMe, boolean isSubscribed, Member member) {
        return new FindMyPageResponse(isMe, isSubscribed,
                new MemberDTO(member.getId(), member.getName(), member.getEmail(), member.getPoint()));
    }

    public record MemberDTO(Long id, String name, String email, int point) {
    }
}
