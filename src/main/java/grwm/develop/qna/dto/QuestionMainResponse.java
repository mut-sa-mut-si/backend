package grwm.develop.qna.dto;

import java.util.List;

public record QuestionMainResponse(
        List<WaitingAnswerQuestion> waitingAnswerQuestion,
        List<CategoryQuestion> questions
) {
    public record WaitingAnswerQuestion(
            Long id,
            String title,
            String content,
            Writer questionWriter
    ) {}

    public record CategoryQuestion(
            Long id,
            String title,
            Writer questionWriter
    ) {}

    public record Writer(
            Long id,
            String name
    ) {}
}
