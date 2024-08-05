package grwm.develop.recipe;

import grwm.develop.auth.security.UserDetailsImpl;
import grwm.develop.recipe.dto.ReadLockRecipeResponse;
import grwm.develop.recipe.dto.ReadRecipeResponse;
import grwm.develop.recipe.dto.RecipeListResponse;
import grwm.develop.recipe.dto.WriteRecipeRequest;
import grwm.develop.recipe.dto.WriteReviewRequest;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    @GetMapping("/default-recipes/unauthentication")
    public ResponseEntity<RecipeListResponse> findAll() {
        RecipeListResponse response = recipeService.findDefaultRecipes(null);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/default-recipes/authentication")
    public ResponseEntity<RecipeListResponse> findAllLoggedIn(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        RecipeListResponse response = recipeService.findDefaultRecipes(userDetails.member());
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/unauthentication")
    public ResponseEntity<RecipeListResponse> list(@RequestParam("category") String category) {
        RecipeListResponse response = recipeService.findRecipeList(null, category);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/authentication")
    public ResponseEntity<RecipeListResponse> listLogin(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                        @RequestParam("category") String category) {
        RecipeListResponse response = recipeService.findRecipeList(userDetails.member(), category);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/{id}/unauthentication")
    public ResponseEntity<ReadRecipeResponse> detailedInqury(@PathVariable("id") Long id) {
        ReadRecipeResponse response = recipeService.findRecipe(id, null);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/{id}/authentication")
    public ResponseEntity<ReadRecipeResponse> detailedInquryLogin(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                                  @PathVariable("id") Long id) {
        ReadRecipeResponse response = recipeService.findRecipe(id, userDetails.member());
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/{id}/lock")
    public ResponseEntity<ReadLockRecipeResponse> detailedInquryLock(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable("id") Long id) {
        ReadLockRecipeResponse response = recipeService.findRockRecipe(userDetails.member(), id);
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/{id}/reviews")
    public ResponseEntity<String> writeReview(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                              @RequestBody WriteReviewRequest writeReviewRequest,
                                              @PathVariable("id") Long id) {
        recipeService.writeReview(userDetails.member(), writeReviewRequest, id);
        return ResponseEntity.ok().body("ok");
    }

    @PostMapping("/{id}/scraps")
    public ResponseEntity<String> clickScrap(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                             @PathVariable("id") Long id) {
        recipeService.clickScrap(userDetails.member(), id);
        return ResponseEntity.ok().body("ok");
    }

    @DeleteMapping("/{id}/scraps")
    public ResponseEntity<String> deleteScrap(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                              @PathVariable("id") Long id) {
        recipeService.deleteScrap(userDetails.member(), id);
        return ResponseEntity.ok().body("ok");
    }

    @PostMapping("/{id}/read-private")
    public ResponseEntity<ReadRecipeResponse> singlePay(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                        @PathVariable("id") Long id) {
        ReadRecipeResponse response = recipeService.buyRecipe(userDetails.member(), id);
        return ResponseEntity.ok().body(response);
    }
}
