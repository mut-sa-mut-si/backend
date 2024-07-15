package grwm.develop.recipe.dto;

import grwm.develop.Category;

import java.util.List;

public record CreateRecipeRequest(String title, String content, Category category, Long memberId, boolean isPublic, List<String> HashTag) {

}
