package grwm.develop.notification;

import grwm.develop.auth.security.UserDetailsImpl;
import grwm.develop.notification.dto.FindAllNotificationsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<FindAllNotificationsResponse> notifications(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        FindAllNotificationsResponse response = notificationService.findAllNotifications(userDetails.member());
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> readNotifications(@PathVariable(name = "id") Long id,
                                                    @RequestParam("type") String type) {

        notificationService.readNotifications(id, type);
        return ResponseEntity.ok().body("ok");
    }
}
