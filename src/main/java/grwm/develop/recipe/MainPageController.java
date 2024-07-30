package grwm.develop.recipe;

import grwm.develop.auth.security.UserDetailsImpl;
import grwm.develop.recipe.dto.MainPageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/main")
public class MainPageController {

    MainPageService mainPageService;

    @GetMapping("authentication")
    public ResponseEntity<MainPageResponse> mainPageLogin(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        MainPageResponse response = mainPageService.findMainPage(userDetails.member());
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("unauthentication")
    public ResponseEntity<MainPageResponse> mainPage() {
        MainPageResponse response = mainPageService.findMainPage(null);
        return ResponseEntity.ok().body(response);
    }
}
