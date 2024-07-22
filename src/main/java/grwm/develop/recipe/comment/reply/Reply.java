package grwm.develop.recipe.comment.reply;

import grwm.develop.BaseEntity;
import grwm.develop.member.Member;
import grwm.develop.recipe.comment.Comment;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

@Entity
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Reply extends BaseEntity {

    @Column(length = 512, nullable = false)
    private String content;

    @JoinColumn(name = "member_id")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Member member;

    @JoinColumn(name = "comment_id")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Comment comment;
}
