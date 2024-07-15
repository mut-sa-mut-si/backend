package grwm.develop.hashtag;

import grwm.develop.hashtag.dto.CreateHashtagRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class HashtagController {

    private final HashtagService hashtagService;

    @PostMapping
    public ResponseEntity<String> create(@RequestBody CreateHashtagRequest request){
        hashtagService.creatHashtag(request);
        return ResponseEntity.status(HttpStatus.CREATED).body("create");
    }
}
