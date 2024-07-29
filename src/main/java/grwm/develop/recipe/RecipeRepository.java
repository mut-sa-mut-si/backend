package grwm.develop.recipe;

import grwm.develop.Category;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    List<Recipe> findAllByMemberId(Long memberId);

    List<Recipe> findAllByCategory(Category category);

    List<Recipe> findByTitleContaining(String keyword);

    List<Recipe> findByContentContaining(String keyword);

}
