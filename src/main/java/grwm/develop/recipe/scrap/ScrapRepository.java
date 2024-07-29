package grwm.develop.recipe.scrap;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ScrapRepository extends JpaRepository<Scrap, Long> {
     Scrap findByMemberId(Long memberId);
     boolean existsByMemberIdAndRecipeId(Long memberId, Long recipeId);
}
