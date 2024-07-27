package grwm.develop.qna.question.dto;

import java.util.List;

public record SearchQuestionResponse(String search, List<SearchQuestion> questions) {
    public record SearchQuestion(Long id, Question question) {
    }
    public record Question(Long id, String title, String content) {
    }
}
