package grwm.develop.recipe.hashtag;

import grwm.develop.recipe.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HashtagRepository extends JpaRepository<Hashtag, Long> {

    List<Hashtag> findByRecipe(Recipe recipe);
}
