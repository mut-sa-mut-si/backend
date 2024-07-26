package grwm.develop.recipe;

import grwm.develop.member.Member;
import grwm.develop.member.MemberRepository;
import grwm.develop.recipe.dto.SearchRecipe;
import grwm.develop.recipe.image.ImageRepository;
import grwm.develop.recipe.review.Review;
import grwm.develop.recipe.review.ReviewRepository;
import grwm.develop.subscribe.Subscribe;
import grwm.develop.subscribe.SubscribeItem;
import grwm.develop.subscribe.SubscribeItemRepository;
import grwm.develop.subscribe.SubscribeRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;

import java.util.List;

public class SearchService {

    RecipeRepository recipeRepository;
    ReviewRepository reviewRepository;
    ImageRepository imageRepository;
    MemberRepository memberRepository;
    SubscribeItemRepository subscribeItemRepository;
    SubscribeRepository subscribeRepository;
    public SearchRecipe searchRecipe(String keyword)
    {
        List<Recipe> recipes = recipeRepository.findByTitleContainingOrContentContaining(keyword);

        SearchRecipe searchRecipe = new SearchRecipe(keyword);
        int num = 0;
        for(Recipe recipe : recipes)
        {
            List<Review> reviews = reviewRepository.findAllByRecipeId(recipe.getId());
            SearchRecipe.FindRecipe findRecipe =
                    new SearchRecipe.FindRecipe(
                            num,
                            reviews.size(),
                            averageRating(reviews),
                            new SearchRecipe.RecipeDetail(
                                    recipe.getId(),
                                    recipe.getTitle(),
                                    imageRepository.findByRecipeId(recipe.getId()).getUrl(),
                                    recipe.isPublic()),
                            new SearchRecipe.MemberDetail(
                                    recipe.getMember().getId(),
                                    recipe.getMember().getName()
                            ));
            searchRecipe.plus(findRecipe);
        }
        return searchRecipe;
    }
    public SearchRecipe searchRecipeLogin(Member member, String category)
    {
        SearchRecipe recipeListResponse = searchRecipe(category);
        for(SearchRecipe.FindRecipe findRecipe: recipeListResponse.getRecipes() )
        {
            Member writer = memberRepository.findById(findRecipe.getMember().getId()).
                    orElseThrow(EntityNotFoundException::new);
            if(findRecipe.getRecipe().isPublic() == false &&
                    isSubscribe(member,writer))
            {
                findRecipe.getRecipe().setPublic(true);
            }
        }
        return recipeListResponse;
    }
    private float averageRating(List<Review> reviews)
    {
        float total = 0f;
        for(Review review : reviews)
        {
            total += review.getRating();
        }
        return total/(float) reviews.size();
    }
    boolean isSubscribe(Member member,Member writer)
    {
        SubscribeItem subscribeItem = subscribeItemRepository.findByMemberId(writer.getId());
        List<Subscribe> subscribes = subscribeRepository.findAllByMemberId(member.getId());
        for(Subscribe subscribe : subscribes)
        {
            if(subscribe.getSubscribeItem().equals(subscribeItem))
            {
                return true;
            }
        }
        return false;
    }

}
