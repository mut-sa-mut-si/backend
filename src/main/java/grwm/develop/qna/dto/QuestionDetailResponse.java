package grwm.develop.qna.dto;

import grwm.develop.member.Member;
import java.util.List;

public record QuestionDetailResponse(
        Long questionId,
        String questionTitle,
        String questionContent,
        Member questionMember,
        List<AnswerDetail> answers) {

    public record AnswerDetail(
            Long answerId,
            String answerContent,
            Member answerMember) {}
}
