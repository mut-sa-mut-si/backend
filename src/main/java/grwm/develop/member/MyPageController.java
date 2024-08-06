package grwm.develop.member;

import grwm.develop.auth.security.UserDetailsImpl;
import grwm.develop.mypage.dto.FindMyPageMineResponse;
import grwm.develop.mypage.dto.FindMyPageResponse;
import grwm.develop.recipe.dto.RecipeListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
public class MyPageController {

    private final MyPageService myPageService;
    private final MemberService memberService;

    @GetMapping
    public ResponseEntity<FindMyPageMineResponse> myPageMine(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        FindMyPageMineResponse response = myPageService.findMyPageMine(userDetails.member());
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FindMyPageResponse> myPage(@PathVariable(name = "id") Long id,
                                                     @AuthenticationPrincipal UserDetailsImpl userDetails) {

        FindMyPageResponse response = myPageService.findMyPage(id, userDetails.member());
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/{id}/recipes")
    public ResponseEntity<RecipeListResponse> myPageRecipes(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                            @PathVariable Long id) {
        RecipeListResponse response = myPageService.myRecipeList(userDetails.member());
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/{id}/scraps")
    public ResponseEntity<RecipeListResponse> myPageScrap(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                          @PathVariable Long id) {
        RecipeListResponse response = myPageService.myScrapList(userDetails.member());
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/{id}/click-subscribe")
    public ResponseEntity<SubscribeResponse> clickSubscribe(@PathVariable Long id) {
        SubscribeResponse response = myPageService.clickSubscribe(id);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/check-onboard")
    public ResponseEntity<Boolean> checkOnboard(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        boolean isOnboarded = memberService.checkMemberOnboarding(userDetails.member());
        return ResponseEntity.ok().body(isOnboarded);
    }
}
