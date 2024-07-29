
package grwm.develop.recipe.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;




@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RecipeListResponse {
    private List<FindRecipe> recipes;
    private String keyword;
    public RecipeListResponse()
    {
        recipes = new ArrayList<>();
        keyword = null;
    }
    public void plus(FindRecipe findRecipe)
    {
        recipes.add(findRecipe);
    }
    public void setKeyword(String keyword)
    {
        this.keyword = keyword;
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