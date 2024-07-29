package grwm.develop.qna.question;

import grwm.develop.auth.security.UserDetailsImpl;
import grwm.develop.qna.dto.QuestionDetailResponse;
import grwm.develop.qna.dto.QuestionMainResponse;
import grwm.develop.qna.question.dto.SearchMyQuestionResponse;
import grwm.develop.qna.question.dto.SearchQuestionRequest;
import grwm.develop.qna.question.dto.SearchQuestionResponse;
import grwm.develop.qna.question.dto.WriteQuestionRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    @PostMapping
    public ResponseEntity<String> createQuestion(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody WriteQuestionRequest request) {
        questionService.writeQuestion(userDetails.member(), request);
        return ResponseEntity.ok().body("ok");
    }

    @GetMapping("{id}")
    public ResponseEntity<QuestionDetailResponse> clickQuestion(@PathVariable(name = "id") Long id) {
        QuestionDetailResponse response = questionService.readQuestion(id);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/search")
    public ResponseEntity<SearchQuestionResponse> searchQuestions(@RequestParam("keyword") String keyword) {
        SearchQuestionRequest request = new SearchQuestionRequest(keyword);
        return ResponseEntity.ok().body(questionService.searchQuestions(request));
    }

    @GetMapping("/my-question")
    public ResponseEntity<SearchMyQuestionResponse> searchMyQuestion(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        SearchMyQuestionResponse response = questionService.searchMyQuestions(userDetails.member());
        return ResponseEntity.ok().body(response);
    }
}
