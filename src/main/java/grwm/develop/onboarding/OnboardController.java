package grwm.develop.onboarding;

import grwm.develop.auth.security.UserDetailsImpl;
import grwm.develop.onboarding.dto.LandingMemberResponse;
import grwm.develop.onboarding.dto.OnboardRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/onboards")
public class OnboardController {

    private final OnboardService onboardService;

    @GetMapping
    public ResponseEntity<LandingMemberResponse> landingPage(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        LandingMemberResponse response = onboardService.landing(userDetails.member());
        return ResponseEntity.ok().body(response);
    }

    @PostMapping
    public ResponseEntity<String> onboardPage(@RequestBody OnboardRequest request,
                                              @AuthenticationPrincipal UserDetailsImpl userDetails) {

        onboardService.onboarding(request, userDetails.member());
        return ResponseEntity.ok().body("ok");
    }
}
