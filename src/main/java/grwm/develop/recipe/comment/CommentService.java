package grwm.develop.recipe.comment;

import grwm.develop.member.Member;
import grwm.develop.recipe.Recipe;
import grwm.develop.recipe.comment.dto.CommentResponse;
import grwm.develop.recipe.comment.dto.WriteCommentRequest;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final RecipeRepository recipeRepository;

    public CommentResponse writeComment(Long recipeId, Member member, WriteCommentRequest request) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(EntityNotFoundException::new);
        Comment comment = buildComment(member, request, recipe);
        commentRepository.save(comment);
        return CommentResponse.from(comment);
    }

    private Comment buildComment(Member member, WriteCommentRequest request, Recipe recipe) {
        return Comment.builder()
                .content(request.content())
                .member(member)
                .recipe(recipe)
                .build();
    }
}