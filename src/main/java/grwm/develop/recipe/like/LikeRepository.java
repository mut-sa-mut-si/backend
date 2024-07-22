package grwm.develop.recipe.like;

import grwm.develop.member.Member;
import grwm.develop.recipe.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like,Long> {

    List<Like> findByRecipe(Recipe recipe);
    boolean existsByMember(Member member);
}
