package grwm.develop.qna.dto;

import java.util.List;

public record QuestionMainResponse(
        List<Faq> faqs,
        List<WaitingAnswerQuestion> waitingAnswerQuestions) {
    public record Faq(
            Long id,
            String title,
            String content,
            Writer questionWriter,
            Writer answerWriter,
            String firstAnswer) {}

    public record Writer(
            Long id,
            String name
    ) {}

    public record WaitingAnswerQuestion(
            Long id,
            String title,
            String content,
            Writer writer
    ) {}
}
