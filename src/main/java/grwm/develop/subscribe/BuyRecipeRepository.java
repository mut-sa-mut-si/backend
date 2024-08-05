package grwm.develop.subscribe;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BuyRecipeRepository extends JpaRepository<buyRecipe, Integer> {
    public boolean existsByMemberIdAndRecipeId(Long memberId, Long recipeId);
}
