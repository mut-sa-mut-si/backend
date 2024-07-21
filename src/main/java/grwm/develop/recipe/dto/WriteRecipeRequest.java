package grwm.develop.recipe.dto;

import java.util.List;

public record WriteRecipeRequest(String title,
                                 String content,
                                 String category,
                                 Boolean isPublic,
                                 List<HashTagDTO> hashtags) {

    public record HashTagDTO(String content) {
    }
}
