package grwm.develop.recipe.image;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImageRepository extends JpaRepository<Image, Long> {
    List<Image> findAllByRecipeId(Long recipeId);
    Image findByRecipeId(Long recipeId);
}
