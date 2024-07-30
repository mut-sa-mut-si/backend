package grwm.develop.recipe.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;


import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
public class MainPageResponse{
    int totalRecipeCount;
    Long joinDate;
    String profileMemberName;
    List<FindReciper> popularRecipers;
    List<RecommendRecipe> recommendRecipes;
    List<RecipeReview> recipeReviews;
    public MainPageResponse(int totalRecipeCount, Long joinDate, String profileMemberName)
    {
        this.totalRecipeCount = totalRecipeCount;
        this.joinDate = joinDate;
        this.profileMemberName =profileMemberName;
        popularRecipers = new ArrayList<>();
        recommendRecipes = new ArrayList<>();
        recipeReviews = new ArrayList<>();
    }
    public void plusRecipers(List<FindReciper> findReciper)
    {
        popularRecipers.addAll(findReciper);
    }
    public void plusRecipes(List<RecommendRecipe> recommendRecipe)
    {
        recommendRecipes.addAll(recommendRecipe);
    }
    public void plusReviews(List<RecipeReview> recipeReview)
    {
        recipeReviews.addAll(recipeReview);
    }
    @Getter
    @AllArgsConstructor
    public static class FindReciper
    {
        int rank;
        int recipeCount;
        int reviewCount;
        float ratingAverage;
        FindMember member;
    }
    @Getter
    @AllArgsConstructor
    public static class RecommendRecipe
    {
        Long id;
        String title;
        String image;
        int reviewCount;
        float ratingAverage;
        FindMember member;
    }
    @Getter
    @AllArgsConstructor
    public static class RecipeReview
    {
        Long id;
        String content;
        float rating;
        FindRecipe recipe;
        FindMember reviewWriter;
        FindMember recipeWriter;
    }
    @Getter
    @AllArgsConstructor
    public static class FindRecipe
    {
        Long id;
        String title;
    }
    @Getter
    @AllArgsConstructor
    public static class FindMember
    {
        Long id;
        String name;
    }


}
