package grwm.develop.member;

import grwm.develop.recipe.Recipe;
import grwm.develop.recipe.RecipeRepository;
import grwm.develop.recipe.dto.RecipeListResponse;
import grwm.develop.recipe.image.Image;
import grwm.develop.recipe.image.ImageRepository;
import grwm.develop.recipe.review.Review;
import grwm.develop.recipe.review.ReviewRepository;
import grwm.develop.recipe.scrap.Scrap;
import grwm.develop.recipe.scrap.ScrapRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MyPageService {

    private final ScrapRepository scrapRepository;
    private final RecipeRepository recipeRepository;
    private final ReviewRepository reviewRepository;
    private final ImageRepository imageRepository;

    private RecipeListResponse buildRecipeList(List<Recipe> recipes) {
        RecipeListResponse recipeListResponse = new RecipeListResponse();
        for (Recipe recipe : recipes) {
            List<Review> reviews = reviewRepository.findAllByRecipeId(recipe.getId());
            RecipeListResponse.FindRecipe findRecipe = new RecipeListResponse.FindRecipe(recipe.getId(), reviews.size(),
                    averageRating(reviews), recipe.getTitle(),
                    imageExist(imageRepository.findAllByRecipeId(recipe.getId()).stream().map(Image::getUrl).toList()),
                    recipe.isPublic(),
                    new RecipeListResponse.MemberDetail(recipe.getMember().getId(), recipe.getMember().getName()));
            recipeListResponse.plus(findRecipe);
        }
        return recipeListResponse;
    }

    public RecipeListResponse myRecipeList(Member member) {
        List<Recipe> recipes = recipeRepository.findAllByMemberId(member.getId());
        RecipeListResponse recipeListResponse = buildRecipeList(recipes);
        return recipeListResponse;
    }

    public RecipeListResponse myScrapList(Member member) {
        List<Scrap> scrapList = scrapRepository.findAllByMemberId(member.getId());
        List<Recipe> recipes = new ArrayList<>();
        for (Scrap scrap : scrapList) {
            recipes.add(scrap.getRecipe());
        }
        RecipeListResponse recipeListResponse = buildRecipeList(recipes);
        return recipeListResponse;
    }

    private float averageRating(List<Review> reviews) {
        float total = 0f;
        for (Review review : reviews) {
            total += review.getRating();
        }
        if (total == 0f) {
            return 0.0f;
        }
        BigDecimal decimal = new BigDecimal(total / (float) reviews.size()).setScale(1, RoundingMode.HALF_UP);
        return decimal.floatValue();
    }

    private String imageExist(List<String> images) {
        if (images.size() > 0) {
            return images.get(0);
        } else {
            return null;
        }
    }
}
