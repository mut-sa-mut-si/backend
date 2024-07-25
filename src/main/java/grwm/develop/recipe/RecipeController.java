package grwm.develop.recipe;

import grwm.develop.auth.security.UserDetailsImpl;
import grwm.develop.recipe.dto.ReadRecipeRequest;
import grwm.develop.recipe.dto.ReadRecipeRequestLogin;
import grwm.develop.recipe.dto.WriteRecipeRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
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
    @GetMapping("/{id}/unauthentication")
    public ResponseEntity<ReadRecipeRequest> detailedInqury(@PathVariable("id") Long id)
    {
            ReadRecipeRequest response = recipeService.findRecipe(id);
            return ResponseEntity.ok().body(response);
    }
    @GetMapping("/{id}/authentication")
    public ResponseEntity<ReadRecipeRequestLogin> detailedInqury(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                                 @PathVariable("id") Long id)
    {
        ReadRecipeRequestLogin response = recipeService.findRecipeLogin(userDetails.member(),id);
        return ResponseEntity.ok().body(response);
    }

}
