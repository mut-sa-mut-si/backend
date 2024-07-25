package grwm.develop.recipe;

import grwm.develop.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    List<Recipe> findAllByMember(Long memberId);
}
