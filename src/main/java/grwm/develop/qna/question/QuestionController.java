package grwm.develop.qna.question;

import grwm.develop.qna.dto.QuestionMainResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/questions")
public class QuestionController {

    private final QuestionService questionService;

    @GetMapping
    public ResponseEntity<QuestionMainResponse> mainQuestionPage(@RequestParam("category") String category)
    {
        QuestionMainResponse response = questionService.getMainPage(category);
        return  ResponseEntity.ok().body(response);
    }
}
