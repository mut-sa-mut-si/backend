package grwm.develop.qna.dto;

import java.util.List;

public record QuestionDetailResponse(
        QuestionDetail question,
        List<AnswerDetail> answers) {

    public record QuestionDetail(
            Long id,
            String title,
            String content,
            MemberDTO member
    ) {
    }

    public record AnswerDetail(
            Long id,
            String content,
            MemberDTO member) {
    }

    public record MemberDTO(Long id, String name) {
    }
}
