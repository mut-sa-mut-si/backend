package grwm.develop.recipe;


import grwm.develop.recipe.dto.CreateRecipeRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/recipes")
@RequiredArgsConstructor
public class RecipeController {
    private final RecipeService recipeController;
    public ResponseEntity<String> create(@RequestBody CreateRecipeRequest request)
    {
        recipeController.createRecipe(request);
        return ResponseEntity.ok().body("ok");
    }
}
