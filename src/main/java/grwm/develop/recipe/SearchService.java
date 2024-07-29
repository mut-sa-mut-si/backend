package grwm.develop.recipe;

import grwm.develop.member.Member;
import grwm.develop.member.MemberRepository;
import grwm.develop.recipe.dto.RecipeListResponse;
import grwm.develop.recipe.dto.SearchPageResponse;
import grwm.develop.recipe.hashtag.Hashtag;
import grwm.develop.recipe.hashtag.HashtagRepository;
import grwm.develop.recipe.image.ImageRepository;
import grwm.develop.recipe.review.Review;
import grwm.develop.recipe.review.ReviewRepository;
import grwm.develop.subscribe.Subscribe;
import grwm.develop.subscribe.SubscribeItem;
import grwm.develop.subscribe.SubscribeItemRepository;
import grwm.develop.subscribe.SubscribeRepository;
import jakarta.persistence.EntityNotFoundException;

import java.util.*;
import java.util.stream.Collectors;

public class SearchService {

    RecipeRepository recipeRepository;
    ReviewRepository reviewRepository;
    ImageRepository imageRepository;
    MemberRepository memberRepository;
    SubscribeItemRepository subscribeItemRepository;
    SubscribeRepository subscribeRepository;
    HashtagRepository hashtagRepository;

    public SearchPageResponse searchPage() {
        List<Hashtag> hashtags = hashtagRepository.findAll();
        Map<String, List<Hashtag>> groupHashtags = groupHashtagsByContent(hashtags);
        List<String> popularKeyword = groupHashtags.keySet().stream().toList();
        SearchPageResponse searchPageResponse = new SearchPageResponse();
        for (String keyword : popularKeyword) {
            SearchPageResponse.findPopularKeyword findPopularKeyword =
                    new SearchPageResponse.
                            findPopularKeyword(hashtagRepository.findByContent(keyword).getId(), keyword);
            searchPageResponse.Plus(findPopularKeyword);
        }
        return searchPageResponse;
    }

    public Map<String, List<Hashtag>> groupHashtagsByContent(List<Hashtag> hashtags) {
        return hashtags.stream().collect(Collectors.groupingBy(Hashtag::getContent))
                .entrySet().stream().sorted((entry1, entry2) ->
                        Integer.compare(entry2.getValue().size(),
                                entry1.getValue().size()))
                .limit(20)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    public RecipeListResponse searchRecipe(String keyword) {
        List<Recipe> recipesContent = recipeRepository.findByContentContaining(keyword);
        List<Recipe> recipesTitle = recipeRepository.findByTitleContaining(keyword);
        List<Recipe> recipesHashtag = new ArrayList<>();
        List<Hashtag> hashtags = hashtagRepository.findByContentContaining(keyword);
        List<Recipe> recipes = integrateRecipe(recipesContent, recipesTitle, recipesHashtag);
        for (Hashtag hashtag : hashtags) {
            Recipe recipe = hashtag.getRecipe();
            recipesHashtag.add(recipe);
        }

        RecipeListResponse searchRecipe = new RecipeListResponse(keyword);
        for (Recipe recipe : recipes) {
            List<Review> reviews = reviewRepository.findAllByRecipeId(recipe.getId());
            RecipeListResponse.FindRecipe findRecipe =
                    new RecipeListResponse.FindRecipe(
                            recipe.getId(),
                            reviews.size(),
                            averageRating(reviews),
                            recipe.getTitle(),
                            imageRepository.findByRecipeId(recipe.getId()).getUrl(),
                            recipe.isPublic(),
                            new RecipeListResponse.MemberDetail(
                                    recipe.getMember().getId(),
                                    recipe.getMember().getName()
                            ));
            searchRecipe.plus(findRecipe);
        }
        return searchRecipe;
    }

    public RecipeListResponse searchRecipeLogin(Member member, String keyword) {
        RecipeListResponse recipeListResponse = searchRecipe(keyword);

        for (RecipeListResponse.FindRecipe findRecipe : recipeListResponse.getRecipes()) {
            Member writer = memberRepository.findById(findRecipe.getMember().getId()).
                    orElseThrow(EntityNotFoundException::new);
            if (!findRecipe.isPublic() &&
                    isSubscribe(member, writer)) {
                findRecipe.setPublic(true);
            }
        }
        return recipeListResponse;
    }

    private float averageRating(List<Review> reviews) {
        float total = 0f;
        for (Review review : reviews) {
            total += review.getRating();
        }
        return total / (float) reviews.size();
    }

    boolean isSubscribe(Member member, Member writer) {
        SubscribeItem subscribeItem = subscribeItemRepository.findByMemberId(writer.getId());
        List<Subscribe> subscribes = subscribeRepository.findAllByMemberId(member.getId());
        for (Subscribe subscribe : subscribes) {
            if (subscribe.getSubscribeItem().equals(subscribeItem)) {
                return true;
            }
        }
        return false;
    }

    public List<Recipe> integrateRecipe(List<Recipe> recipesContent,
                                        List<Recipe> recipesHashtag,
                                        List<Recipe> recipesTitle) {
        Set<Recipe> recipeSet = new LinkedHashSet<>();

        recipeSet.addAll(recipesContent);
        recipeSet.addAll(recipesHashtag);
        recipeSet.addAll(recipesTitle);

        return new ArrayList<>(recipeSet);
    }

}