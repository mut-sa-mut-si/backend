package grwm.develop.recipe;

import grwm.develop.auth.security.UserDetailsImpl;
import grwm.develop.recipe.dto.RecipeListResponse;
import grwm.develop.recipe.dto.WriteRecipeRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/recipes")
public class RecipeController {

    private final RecipeService recipeService;

    @PostMapping
    public ResponseEntity<String> write(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                        @RequestPart("recipe") WriteRecipeRequest request,
                                        @RequestPart(name = "images", required = false) List<MultipartFile> images) {

        recipeService.writeRecipe(userDetails.member(), request, images);
        return ResponseEntity.ok().body("ok");
    }

    @GetMapping("/unauthentication")
    public ResponseEntity<RecipeListResponse> list(@RequestParam("category") String category) {
        RecipeListResponse response = recipeService.findRecipeList(category);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/authentication")
    public ResponseEntity<RecipeListResponse> listLogin(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                        @RequestParam("category") String category) {

        RecipeListResponse response = recipeService.findRecipeListLogin(userDetails.member(), category);
        return ResponseEntity.ok().body(response);
    }
}
