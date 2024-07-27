package grwm.develop.qna.answer;

import grwm.develop.auth.security.UserDetailsImpl;
import grwm.develop.qna.answer.dto.WriteAnswerRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/questions")
public class AnswerController {

    private final AnswerService answerService;

    @PostMapping("/{id}/answers")
    public ResponseEntity<String> createAnswer(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody WriteAnswerRequest request) {
        answerService.writeAnswer(id, userDetails.member(), request);
        return ResponseEntity.ok().body("ok");
    }
}
