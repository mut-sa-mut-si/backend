package grwm.develop.recipe.dto;

import grwm.develop.member.Member;

public record ReadLockRecipe(int point, FindMember member) {

    static public ReadLockRecipe of(int point, Member member)
    {
        return new ReadLockRecipe(point,
                new FindMember(member.getId(),member.getName()));
    }

    record FindMember(Long id, String name){

    }
}
