package grwm.develop.recipe.comment;

import grwm.develop.recipe.Recipe;
import grwm.develop.recipe.like.Like;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment,Long> {
    List<Comment> findByRecipe(Recipe recipe);
}
