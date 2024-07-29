package grwm.develop.qna.question.dto;

import java.util.List;

public record SearchMyQuestionResponse(List<Question> questions) {
    public record Question(long id, String title, String content, Member member) {}

    public record Member(long id, String name) {}
}
