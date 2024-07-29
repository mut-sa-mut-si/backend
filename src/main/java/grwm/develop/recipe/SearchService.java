package grwm.develop.recipe;

import grwm.develop.recipe.dto.SearchPageResponse;
import grwm.develop.recipe.hashtag.Hashtag;
import grwm.develop.recipe.hashtag.HashtagRepository;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SearchService {

    private final HashtagRepository hashtagRepository;

    public SearchPageResponse searchPage() {
        List<Hashtag> hashtags = hashtagRepository.findAll();
        Map<String, List<Hashtag>> groupHashtags = groupHashtagsByContent(hashtags);
        return SearchPageResponse.from(groupHashtags);
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
