package grwm.develop.recipe.image;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {
    List<Image> findAllByRecipeId(Long recipeId);
    Image findByRecipeId(Long recipeId);
}
