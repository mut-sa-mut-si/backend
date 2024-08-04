package grwm.develop.mypage.dto;

import grwm.develop.member.Member;

import java.util.List;

public record FindMyPageResponse(boolean isMe, boolean isSubscribed,int recipeCount, float ratingAverage, MemberDTO member, List<RecipeDTO> recipes) {

    public static FindMyPageResponse of(boolean isMe, boolean isSubscribed,int recipeCount,float ratingAverage, Member member,List<RecipeDTO> recipes) {
        return new FindMyPageResponse(isMe, isSubscribed, recipeCount, ratingAverage,
                new MemberDTO(member.getId(), member.getName(), member.getEmail(), member.getPoint()),
                recipes);

    }

    public record RecipeDTO(Long id, String image, boolean isPublic){
    }

    public record MemberDTO(Long id, String name, String email, int point) {
    }
}
