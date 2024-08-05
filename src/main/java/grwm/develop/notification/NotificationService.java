package grwm.develop.notification;

import grwm.develop.member.Member;
import grwm.develop.notification.dto.FindAllNotificationsResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {

    private final RecipeNotificationRepository recipeNotificationRepository;
    private final AnswerNotificationRepository answerNotificationRepository;

    public FindAllNotificationsResponse findAllNotifications(Member member) {
        List<RecipeNotification> recipeNotifications = recipeNotificationRepository.findAllByMemberId(member.getId());
        List<AnswerNotification> answerNotifications = answerNotificationRepository.findAllByMemberId(member.getId());
        return FindAllNotificationsResponse.from(recipeNotifications, answerNotifications);
    }

    @Transactional
    public void readNotifications(Long id, String type) {
        if (type.equals("RECIPE") || type.equals("REVIEW")) {
            recipeNotificationRepository.deleteById(id);
        }
        if (type.equals("ANSWER")) {
            answerNotificationRepository.deleteById(id);
        }
    }
}
