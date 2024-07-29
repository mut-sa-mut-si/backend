package grwm.develop.qna.dto;

import java.util.List;

public record QuestionMainResponse(
        List<WaitingAnswerQuestion> waitingAnswerQuestions,
        List<CategoryQuestion> categoryQuestions) {
    public record WaitingAnswerQuestion(
            Long id,
            String title,
            String content,
            Writer member
    ) {}

    public record CategoryQuestion(
            Long id,
            String title,
            Writer member
    ) {}

    public record Writer(
            Long id,
            String name
    ) {}
}
