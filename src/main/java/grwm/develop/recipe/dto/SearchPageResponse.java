package grwm.develop.recipe.dto;

import java.util.ArrayList;
import java.util.List;

public class SearchPageResponse {

    List<findPopularKeyword> popularKewords;

    public SearchPageResponse() {
        popularKewords = new ArrayList<>();
    }

    public void Plus(findPopularKeyword findPopularKeyword) {
        popularKewords.add(findPopularKeyword);
    }

    public static class findPopularKeyword {
        Long id;
        String keword;

        public findPopularKeyword(Long id, String keyword) {
            this.id = id;
            this.keword = keyword;
        }
    }
}