package grwm.develop.recipe.comment.reply.dto;

import grwm.develop.recipe.comment.reply.Reply;

public record ReplyResponse(Long id, String content, FindMember member, String createdAt, Long commentId) {

    public static ReplyResponse from(Reply reply) {
        return new ReplyResponse(
                reply.getId(),
                reply.getContent(),
                new FindMember(
                        reply.getMember().getId(),
                        reply.getMember().getName(),
                        "기본이미지"
                ),
                reply.getCreatedAt().toString(),
                reply.getComment().getId()
        );
    }

    public record FindMember(Long id, String name, String image) {
    }
}