package grwm.develop.notification;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerNotificationRepository extends JpaRepository<AnswerNotification, Long> {

    List<AnswerNotification> findAllByMemberId(Long memberId);
}
