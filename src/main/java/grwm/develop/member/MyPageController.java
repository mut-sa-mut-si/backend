package grwm.develop.member;

import grwm.develop.auth.security.UserDetailsImpl;
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
}
