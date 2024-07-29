package grwm.develop.recipe;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    List<Recipe> findAllByMemberId(Long memberId);
    List<Recipe> findAllByCategory(String category);
    List<Recipe> findByTitleContaining(String keyword);
    List<Recipe> findByContentContaining(String keyword);

}
