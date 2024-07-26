package grwm.develop.recipe.hashtag;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HashtagRepository extends JpaRepository<Hashtag, Long> {

    List<Hashtag> findAllByRecipeId(Long recipeId);
}
