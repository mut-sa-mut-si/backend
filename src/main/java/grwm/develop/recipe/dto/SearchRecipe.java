package grwm.develop.recipe.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class SearchRecipe {

    private String search;
    private List<FindRecipe> recipes;

    public SearchRecipe(String search) {
        this.search = search;
        recipes = new ArrayList<>();
    }

    public void plus(FindRecipe findRecipe) {
        recipes.add(findRecipe);
    }

    @Getter
    @AllArgsConstructor
    public static class FindRecipe {

        private Long id;
        private int reviewCount;
        private float ratingAverage;
        private String title;
        private String image;
        private boolean isPublic;
        private MemberDetail member;

        public void SetisPublic(boolean isPublic) {
            this.isPublic = isPublic;
        }

    }

    @Getter
    @AllArgsConstructor
    public static class MemberDetail {
        private Long id;
        private String name;
    }

}
