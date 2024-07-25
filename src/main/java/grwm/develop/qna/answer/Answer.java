package grwm.develop.qna.answer;

import grwm.develop.BaseEntity;
import grwm.develop.qna.question.Question;
import grwm.develop.member.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Answer extends BaseEntity {

    @Column(length = 2048, nullable = false)
    private String content;

    @JoinColumn(name = "member_id")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Member member;

    @JoinColumn(name = "question_id")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Question question;
}
