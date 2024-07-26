package grwm.develop.recipe.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class SearchRecipe{

    private String search;
    private List<FindRecipe> recipes;
    public SearchRecipe(String search)
    {
        this.search = search;
        recipes = new ArrayList<>();
    }
    public void plus(FindRecipe findRecipe)
    {
        recipes.add(findRecipe);
    }
    @Getter
    @AllArgsConstructor
    public static class FindRecipe{

        private int id;
        private int reviewCount;
        private float ratingAverage;
        private RecipeDetail recipe;
        private MemberDetail member;
    }
    @Getter
    @AllArgsConstructor
    public static class RecipeDetail{
        private Long id;
        private String title;
        private String image;
        private boolean isPublic;
        public void setPublic(boolean setPublic)
        {
            this.isPublic = setPublic;
        }
    }
    @Getter
    @AllArgsConstructor
    public static class MemberDetail{
        private Long id;
        private String name;
    }

}
