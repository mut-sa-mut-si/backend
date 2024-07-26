package grwm.develop.recipe.dto;

import grwm.develop.member.Member;

public record ReadLockRecipeResponse(int point, FindMember member) {

    static public ReadLockRecipeResponse of(int point, Member member)
    {
        return new ReadLockRecipeResponse(point,
                new FindMember(member.getId(),member.getName()));
    }

    record FindMember(Long id, String name){

    }
}
