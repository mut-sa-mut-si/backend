package grwm.develop.recipe.scrap;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ScrapRepository extends JpaRepository<Scrap, Long> {
     boolean existsByMemberIdAndRecipeId(Long memberId, Long recipeId);
     Optional<Scrap> findByMemberIdAndRecipeId(Long memberId, Long recipeId);
     List<Scrap> findAllByMemberId(Long memberId);
}
