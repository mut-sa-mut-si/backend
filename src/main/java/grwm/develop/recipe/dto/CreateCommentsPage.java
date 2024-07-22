package grwm.develop.recipe.dto;

import grwm.develop.member.Member;
import grwm.develop.recipe.comment.Comment;
import grwm.develop.recipe.comment.reply.Reply;

import java.time.LocalDateTime;
import java.util.List;

public record CreateCommentsPage(FindComment comment, List<FindReply> replyComments) {


    public static CreateCommentsPage of(Comment comment, List<Reply> replies) {
        return new CreateCommentsPage(
                new FindComment(comment.getId(), comment.getContent(), new FindMember(comment.getMember().getId(),
                        comment.getMember().getName(),
                        "기본이미지"), comment.getCreatedAt()),
                replies.stream().map(reply ->
                        new FindReply(
                                reply.getId(),
                                reply.getContent(),
                                new FindMember(reply.getMember().getId(),
                                        reply.getMember().getName(),
                                        "기본이미지"),
                                reply.getCreatedAt())).toList()
        );
    }

    private record FindComment(Long id, String content, FindMember member, LocalDateTime CreatedAt) {

    }

    private record FindReply(Long id, String content, FindMember member, LocalDateTime createdAt) {

    }

    public record FindMember(Long id, String name, String image) {

    }
}
