package grwm.develop.recipe.comment;

import grwm.develop.member.Member;
import grwm.develop.notification.Type;
import grwm.develop.notification.active.Active;
import grwm.develop.notification.active.ActiveReceiver;
import grwm.develop.notification.active.ActiveReceiverRepository;
import grwm.develop.notification.active.ActiveRepository;
import grwm.develop.recipe.Recipe;
import grwm.develop.recipe.comment.dto.CommentResponse;
import grwm.develop.recipe.comment.dto.WriteCommentRequest;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final RecipeRepository recipeRepository;
    private final ActiveRepository activeRepository;
    private final ActiveReceiverRepository activeReceiverRepository;

    @Transactional
    public CommentResponse writeComment(Long recipeId, Member member, WriteCommentRequest request) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(EntityNotFoundException::new);
        Comment comment = buildComment(member, request, recipe);
        commentRepository.save(comment);

        createNotification(recipe, comment);

        return CommentResponse.from(comment);
    }

    private Comment buildComment(Member member, WriteCommentRequest request, Recipe recipe) {
        return Comment.builder()
                .content(request.content())
                .member(member)
                .recipe(recipe)
                .build();
    }

    private void createNotification(Recipe recipe, Comment comment) {
        Active active = buildActive(recipe, comment);
        ActiveReceiver activeReceiver = buildActiveReceiver(recipe, active);
        activeRepository.save(active);
        activeReceiverRepository.save(activeReceiver);
    }

    private Active buildActive(Recipe recipe, Comment comment) {
        Member member = comment.getMember();
        return Active.builder()
                .member(member)
                .recipe(recipe)
                .build();
    }

    private ActiveReceiver buildActiveReceiver(Recipe recipe, Active active) {
        Member member = recipe.getMember();
        return ActiveReceiver.builder()
                .active(active)
                .member(member)
                .type(Type.COMMENT)
                .build();
    }
}
