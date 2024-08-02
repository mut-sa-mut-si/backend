package grwm.develop.mypage;

import grwm.develop.auth.security.UserDetailsImpl;
import grwm.develop.mypage.dto.FindAllSubscribesResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/main")
public class MyPageManageController {

    private final MyPageManageService myPageManageService;

    @GetMapping("/{id}/subscribes")
    public ResponseEntity<FindAllSubscribesResponse> subscribes(@PathVariable(name = "id") Long id,
                                                                @AuthenticationPrincipal UserDetailsImpl userDetails) {

        FindAllSubscribesResponse response = myPageManageService.findAllSubscribes(userDetails.member());
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping("/{memberId}/subscribes/{subscribeId}")
    public ResponseEntity<String> cancelSubscribe(@PathVariable(name = "memberId") Long memberId,
                                                  @PathVariable(name = "subscribeId") Long subscribeId,
                                                  @AuthenticationPrincipal UserDetailsImpl userDetails) {

        myPageManageService.deleteSubscribe(subscribeId, userDetails.member());
        return ResponseEntity.ok().body("ok");
    }
}
