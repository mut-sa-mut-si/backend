package grwm.develop.recipe;

import grwm.develop.Category;
import grwm.develop.member.Member;
import grwm.develop.member.MemberRepository;
import grwm.develop.onboarding.Onboard;
import grwm.develop.onboarding.OnboardRepository;
import grwm.develop.recipe.dto.MainPageResponse;
import grwm.develop.recipe.hashtag.Hashtag;
import grwm.develop.recipe.hashtag.HashtagRepository;
import grwm.develop.recipe.image.Image;
import grwm.develop.recipe.image.ImageRepository;
import grwm.develop.recipe.review.Review;
import grwm.develop.recipe.review.ReviewRepository;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MainPageService {

    private final RecipeService recipeService;

    @Getter
    @AllArgsConstructor
    static class Pair<K, V> {
        private K key;
        private V value;
    }

    static final int RECOMMEND_RECIPECOUNT = 6;
    static final int POPULAR_RECIPERCOUNT = 3;
    static final int POPULAR_REVIEWCOUNT = 3;

    private final RecipeRepository recipeRepository;
    private final MemberRepository memberRepository;
    private final ReviewRepository reviewRepository;
    private final OnboardRepository onboardRepository;
    private final HashtagRepository hashtagRepository;
    private final ImageRepository imageRepository;

    private List<MainPageResponse.FindReciper> buildFindReciperList(List<Member> popularRecipers) {
        int rank = 1;
        List<MainPageResponse.FindReciper> recipers = new ArrayList<>();
        for (Member member : popularRecipers) {
            List<Recipe> recipes = recipeRepository.findAllByMemberId(member.getId());
            List<Review> reviews = new ArrayList<>();
            for (Recipe recipe : recipes) {
                reviews.addAll(reviewRepository.findAllByRecipeId(recipe.getId()));
            }
            averageRatingOfReview(reviews);

            recipers.add(new MainPageResponse.FindReciper(
                    rank,
                    recipes.size(),
                    reviews.size(),
                    averageRatingOfReview(reviews),
                    new MainPageResponse.FindMember(member.getId(), member.getName())));
            rank++;
        }
        return recipers;
    }

    private List<MainPageResponse.RecommendRecipe> buildRecommendRecipeList(List<Recipe> popularRecipers) {
        List<MainPageResponse.RecommendRecipe> recommendRecipes = new ArrayList<>();
        for (Recipe recipe : popularRecipers) {
            if (recipe.isPublic()) {
                recommendRecipes.add(
                        new MainPageResponse.RecommendRecipe(
                                recipe.getId(),
                                recipe.getTitle(),
                                imageExist(imageRepository.findAllByRecipeId(recipe.getId()).stream().map(Image::getUrl)
                                        .toList()),
                                reviewRepository.findAllByRecipeId(recipe.getId()).size(),
                                averageRatingOfReview(reviewRepository.findAllByRecipeId(recipe.getId())),
                                new MainPageResponse.FindMember(recipe.getMember().getId(),
                                        recipe.getMember().getName())));
            }
        }
        return recommendRecipes;
    }

    private List<MainPageResponse.RecipeReview> buildRecipeReviewList(List<Review> popularReviews) {
        List<MainPageResponse.RecipeReview> reviews = new ArrayList<>();
        for (Review review : popularReviews) {
            reviews.add(new MainPageResponse.RecipeReview(
                    review.getId(),
                    review.getContent(),
                    Math.round(review.getRating() * 10.0f) / 10.0f,
                    new MainPageResponse.FindRecipe(review.getRecipe().getId(),
                            review.getRecipe().getTitle()),
                    new MainPageResponse.FindMember(review.getMember().getId(),
                            review.getMember().getName()),
                    new MainPageResponse.FindMember(review.getRecipe().getMember().getId(),
                            review.getRecipe().getMember().getName())));
        }
        return reviews;
    }

    private MainPageResponse buildMainPageResponse(
            int totalRecipeCount,
            Long joinDate,
            String profileMemberName,
            List<Member> popularRecipers,
            List<Recipe> popularRecipes,
            List<Review> popularReviews) {
        MainPageResponse mainPageResponse = new MainPageResponse(totalRecipeCount, joinDate, profileMemberName);
        List<MainPageResponse.FindReciper> recipers = buildFindReciperList(popularRecipers);
        List<MainPageResponse.RecommendRecipe> recommendRecipes = buildRecommendRecipeList(popularRecipes);
        List<MainPageResponse.RecipeReview> reviews = buildRecipeReviewList(popularReviews);

        mainPageResponse.plusRecipers(recipers);
        mainPageResponse.plusRecipes(recommendRecipes);
        mainPageResponse.plusReviews(reviews);
        return mainPageResponse;
    }

    public MainPageResponse findMainPage(Member member) {
        int totalRecipeCount = recipeRepository.findAll().size();
        List<Member> popularRecipers = findPopularRecipers();
        List<Recipe> recommendRecipes = findRecommendRecipes(member);
        List<Review> recipeReviews = findReviews();
        Long joinDate = member != null ? ChronoUnit.DAYS.between(member.getCreatedAt(), LocalDateTime.now()) : null;
        String profileMemberName = member != null ? member.getName() : null;
        return buildMainPageResponse(totalRecipeCount, joinDate, profileMemberName,
                popularRecipers, recommendRecipes, recipeReviews);
    }

    private List<Member> findPopularRecipers() {
        List<Member> members = memberRepository.findAll();
        return bestRecipers(members).stream().limit(POPULAR_RECIPERCOUNT).collect(Collectors.toList());
    }

    private List<Recipe> findRecommendRecipes(Member member) {
        List<Recipe> recipes = new ArrayList<>();
        if (member == null) {//만약에 로그인을 안했던 유저라면 랜덤으로 6개 반환하기
            return findRandomValueList(recipeRepository.findAll(), RECOMMEND_RECIPECOUNT);
        }
        Map<Category, List<Onboard>> memberOnboard = classifyOnboard(member);
        for (Category category : memberOnboard.keySet()) {
            List<Recipe> recipeList = recipeRepository.findAllByCategory(category);
            List<Onboard> onboards = memberOnboard.get(category);
            recipes.addAll(bestRecipe(recipeList, onboards, RECOMMEND_RECIPECOUNT / memberOnboard.size()));
        }
        if((recipes.size() < RECOMMEND_RECIPECOUNT) && (recipeRepository.findAll().size() >= RECOMMEND_RECIPECOUNT)) {
            //추천게시물의 수가 6개보다 작고 들어있는 레시피의 총 수가 6개 이상일때 실행한다.
           for(Recipe recipe : recipeRepository.findAll()) {
               if(!recipes.contains(recipe))
               {
                   recipes.add(recipe);
               }
               if(recipes.size() == RECOMMEND_RECIPECOUNT) {
                   break;
               }
           }
        }
        return recipes;
    }

    private List<Review> findReviews() {
        List<Review> reviews = reviewRepository.findAll();
        return findRandomValueList(reviews, POPULAR_REVIEWCOUNT);
    }

    private <T> List<T> findRandomValueList(List<T> list, int Count) {
        Collections.shuffle(list);
        return list.size() > Count ? list.subList(0, Count) : list;
    }

    private Map<Category, List<Onboard>> classifyOnboard(Member member) {
        List<Onboard> memberOnboard = onboardRepository.findAllByMemberId(member.getId());
        Map<Category, List<Onboard>> classify = new HashMap<>();
        for (Onboard onboard : memberOnboard) {
            if (classify.containsKey(onboard.getCategory())) {
                classify.get(onboard.getCategory()).add(onboard);
            } else {
                List<Onboard> onboards = new ArrayList<>();
                onboards.add(onboard);
                classify.put(onboard.getCategory(), onboards);
            }
        }
        return classify;
    }

    private List<Recipe> bestRecipe(List<Recipe> recipes, List<Onboard> onboards, int recipeCount) {
        List<Pair<Float, Recipe>> bestRecipes = new ArrayList<>();
        for (Recipe recipe : recipes) {
            if (findRecipeOfOnboard(recipe, onboards)) {
                bestRecipes.add(
                        new Pair<>(averageRatingOfReview(reviewRepository.findAllByRecipeId(recipe.getId())), recipe));
            }
        }
        Collections.sort(bestRecipes, Comparator.comparing(Pair::getKey));
        return bestRecipes.stream().limit(recipeCount).map(Pair::getValue).collect(Collectors.toList());
    }

    private List<Member> bestRecipers(List<Member> members) {

        List<Pair<Float, Member>> memberList = new ArrayList<>();
        for (Member member : members) {
            List<Recipe> recipes = recipeRepository.findAllByMemberId(member.getId());
            List<Review> reviews = new ArrayList<>();
            for (Recipe recipe : recipes) {
                List<Review> reviewsOfRecipe = reviewRepository.findAllByRecipeId(recipe.getId());
                reviews.addAll(reviewsOfRecipe);
            }
            memberList.add(new Pair<>(averageRatingOfReview(reviews), member));
        }
        memberList.sort((pair1, pair2) -> Float.compare(pair2.getKey(), pair1.getKey()));
        return memberList.stream().map(Pair::getValue).collect(Collectors.toList());
    }

    private float averageRatingOfReview(List<Review> reviews) {
        if (reviews.isEmpty()) {
            return 0;
        }
        float total = 0f;
        for (Review review : reviews) {
            total += review.getRating();
        }
        return Math.round((total / (float) reviews.size()) * 10.0f) / 10.0f;
    }

    private boolean findRecipeOfOnboard(Recipe recipe, List<Onboard> onboards) {
        List<Hashtag> hashtags = hashtagRepository.findAllByRecipeId(recipe.getId());
        for (Onboard onboard : onboards) {
            String keyword = onboard.getKeyword();
            if (recipe.getContent().contains(keyword)) {
                return true;
            }
            for (Hashtag hashtag : hashtags) {
                if (hashtag.getContent().contains(onboard.getKeyword())) {
                    return true;
                }
            }
        }
        return false;
    }

    private String imageExist(List<String> images) {
        if (images.size() > 0) {
            return images.get(0);
        } else {
            return null;
        }
    }
}
