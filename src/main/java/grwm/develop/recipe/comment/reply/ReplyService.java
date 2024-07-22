package grwm.develop.recipe.comment.reply;

import grwm.develop.member.Member;
import grwm.develop.recipe.comment.Comment;
import grwm.develop.recipe.comment.CommentRepository;
import grwm.develop.recipe.comment.reply.dto.WriteReplyRequest;
import grwm.develop.recipe.comment.reply.dto.ReplyResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReplyService {

    private final ReplyRepository replyRepository;
    private final CommentRepository commentRepository;

    public ReplyResponse writeReply(Long commentId, Member member, WriteReplyRequest request) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(EntityNotFoundException::new);
        Reply reply = buildReply(member, request, comment);
        replyRepository.save(reply);

        return ReplyResponse.from(reply);
    }

    private Reply buildReply(Member member, WriteReplyRequest request, Comment comment) {
        return Reply.builder()
                .content(request.content())
                .member(member)
                .comment(comment)
                .build();
    }
}