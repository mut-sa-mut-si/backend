package grwm.develop.recipe.dto;

import grwm.develop.member.Member;
import grwm.develop.recipe.Recipe;
import grwm.develop.recipe.review.Review;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;




@Getter
public class RecipeListResponse {
    private List<FindRecipe> recipes;
    public RecipeListResponse()
    {
        recipes = new ArrayList<>();
    }
    public void plus(FindRecipe findRecipe)
    {
        recipes.add(findRecipe);
    }
    @Getter
    @AllArgsConstructor
    public static class FindRecipe{
        private Long id;
        private int reviewCount;
        private float ratingAverage;
        private String title;
        private String image;
        private boolean isPublic;
        private MemberDetail member;
        public void setPublic(boolean value)
        {
            this.isPublic = value;
        }
    }
    @Getter
    @AllArgsConstructor
    public static class MemberDetail{
        private Long id;
        private String name;
    }

}