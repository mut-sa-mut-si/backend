package grwm.develop.recipe.comment.dto;

import grwm.develop.recipe.comment.Comment;

public record CommentResponse(Long id, String content, FindMember member, String createdAt) {

    public static CommentResponse from(Comment comment) {
        return new CommentResponse(
                comment.getId(),
                comment.getContent(),
                new FindMember(
                        comment.getMember().getId(),
                        comment.getMember().getName(),
                        "기본이미지"),
                comment.getCreatedAt().toString()
        );
    }

    public record FindMember(Long id, String name, String image) {
    }
}
