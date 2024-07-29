package grwm.develop.recipe;

import grwm.develop.recipe.dto.SearchPageResponse;
import grwm.develop.recipe.hashtag.Hashtag;
import grwm.develop.recipe.hashtag.HashtagRepository;


import java.util.*;
import java.util.stream.Collectors;

public class SearchService {

    HashtagRepository hashtagRepository;

    public SearchPageResponse searchPage() {
        List<Hashtag> hashtags = hashtagRepository.findAll();
        Map<String, List<Hashtag>> groupHashtags = groupHashtagsByContent(hashtags);
        List<String> popularKeyword = groupHashtags.keySet().stream().toList();
        SearchPageResponse searchPageResponse = new SearchPageResponse();
        for (String keyword : popularKeyword) {
            SearchPageResponse.findPopularKeyword findPopularKeyword =
                    new SearchPageResponse.
                            findPopularKeyword(hashtagRepository.findByContent(keyword).getId(), keyword);
            searchPageResponse.Plus(findPopularKeyword);
        }
        return searchPageResponse;
    }

    public Map<String, List<Hashtag>> groupHashtagsByContent(List<Hashtag> hashtags) {
        return hashtags.stream().collect(Collectors.groupingBy(Hashtag::getContent))
                .entrySet().stream().sorted((entry1, entry2) ->
                        Integer.compare(entry2.getValue().size(),
                                entry1.getValue().size()))
                .limit(20)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }


}