package grwm.develop.notification;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipeNotificationRepository extends JpaRepository<RecipeNotification, Long> {

    List<RecipeNotification> findAllByMemberId(Long memberId);
}
