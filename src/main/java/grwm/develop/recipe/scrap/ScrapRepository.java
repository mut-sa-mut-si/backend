package grwm.develop.recipe.scrap;

import grwm.develop.member.Member;
import grwm.develop.recipe.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ScrapRepository extends JpaRepository<Scrap,Long> {
    List<Scrap> findByRecipe(Recipe recipe);
    Member findByMember(Member member);
}
