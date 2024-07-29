package grwm.develop.recipe.dto;

import grwm.develop.recipe.hashtag.Hashtag;
import java.util.List;
import java.util.Map;

public record SearchPageResponse(List<KeywordDTO> popularKeywords) {

    public static SearchPageResponse from(Map<String, List<Hashtag>> hashtagMap) {
        return new SearchPageResponse(
                hashtagMap.entrySet().stream()
                        .map(entry -> new KeywordDTO(entry.getValue().get(0).getId(), entry.getKey()))
                        .toList()
        );
    }

    public record KeywordDTO(Long id, String keyWord) {
    }
}