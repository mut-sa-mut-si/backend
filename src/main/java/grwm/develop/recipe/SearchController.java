package grwm.develop.recipe;

import grwm.develop.auth.security.UserDetailsImpl;
import grwm.develop.recipe.dto.SearchRecipe;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/search")
public class SearchController {

    SearchService searchService;
    @GetMapping
    public ResponseEntity<String> searchPage()
    {
        return ResponseEntity.ok().body("굿");
    }
    @GetMapping("/unauthentication")
    public ResponseEntity<SearchRecipe> searchRecipe(@RequestParam("keyword") String keyword)
    {
        SearchRecipe response = searchService.searchRecipe(keyword);
        return ResponseEntity.ok().body(response);
    }
    @GetMapping("/authentication")
    public ResponseEntity<SearchRecipe> searchRecipe(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                     @RequestParam("keyword")String keyword)
    {
        SearchRecipe response = searchService.searchRecipeLogin(userDetails.member(),keyword);
        return ResponseEntity.ok().body(response);
    }
}
