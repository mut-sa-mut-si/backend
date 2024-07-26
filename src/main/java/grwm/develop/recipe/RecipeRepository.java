package grwm.develop.recipe;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    List<Recipe> findByTitleContaining(String keyword);
    List<Recipe> findByContentContaining(String keyword);

}
