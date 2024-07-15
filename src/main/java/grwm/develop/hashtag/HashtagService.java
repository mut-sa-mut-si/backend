package grwm.develop.hashtag;

import grwm.develop.hashtag.dto.CreateHashtagRequest;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class HashtagService {

    private final HashtagRepository hashtagRepository;

    @Transactional
    public void creatHashtag(CreateHashtagRequest request){
        String createContent = request.content();
        Long recipeId = request.recipeId();

        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(EntityNotFoundException::new);

        Hashtag hashtag = new Hashtag(createContent, recipe);
        hashtagRepository.save(hashtag);
    }
}
