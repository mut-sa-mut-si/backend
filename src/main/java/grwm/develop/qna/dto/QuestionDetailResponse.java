package grwm.develop.qna.dto;

import grwm.develop.member.Member;
import java.util.List;

public record QuestionDetailResponse(
        QuestionDetail question,
        List<AnswerDetail> answers) {

    public record QuestionDetail(
            Long Id,
            String Title,
            String Content,
            Member Member
    ) {}

    public record AnswerDetail(
            Long Id,
            String Content,
            Member Member) {}
}
