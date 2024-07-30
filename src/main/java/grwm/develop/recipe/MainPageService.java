package grwm.develop.recipe;

import grwm.develop.Category;
import grwm.develop.member.Member;
import grwm.develop.member.MemberRepository;
import grwm.develop.onboarding.Onboard;
import grwm.develop.onboarding.OnboardRepository;
import grwm.develop.recipe.dto.MainPageResponse;
import grwm.develop.recipe.hashtag.Hashtag;
import grwm.develop.recipe.hashtag.HashtagRepository;
import grwm.develop.recipe.image.ImageRepository;
import grwm.develop.recipe.review.Review;
import grwm.develop.recipe.review.ReviewRepository;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

public class MainPageService {

    static final int recommendRecipeCount = 6;
    static final int popularReciperCount = 3;
    static final int popularReviewCount = 3;

    RecipeRepository recipeRepository;
    MemberRepository memberRepository;
    ReviewRepository reviewRepository;
    OnboardRepository onboardRepository;
    HashtagRepository hashtagRepository;
    ImageRepository imageRepository;

    private List<MainPageResponse.FindReciper> buildFindReciperList(List<Member> popularRecipers) {
        int rank = 1;
        List<MainPageResponse.FindReciper> recipers = new ArrayList<>();
        for (Member member : popularRecipers) {
            List<Recipe> recipes = recipeRepository.findAllByMemberId(member.getId());
            List<Review> reviews = new ArrayList<>();
            for (Recipe recipe : recipes) {
                reviews.addAll(reviewRepository.findAllByRecipeId(recipe.getId()));
            }
            averageRating(reviews);

            recipers.add(new MainPageResponse.FindReciper(
                    rank,
                    recipes.size(),
                    reviews.size(),
                    averageRating(reviews),
                    new MainPageResponse.FindMember(member.getId(), member.getName())));
            rank++;
        }
        return recipers;
    }

    private List<MainPageResponse.RecommendRecipe> buildRecommendRecipeList(List<Recipe> popularRecipers) {
        List<MainPageResponse.RecommendRecipe> recommendRecipes = new ArrayList<>();
        for (Recipe recipe : popularRecipers) {
            recommendRecipes.add(
                    new MainPageResponse.RecommendRecipe(
                            recipe.getId(),
                            recipe.getTitle(),
                            imageRepository.findByRecipeId(recipe.getId()).getUrl(),
                            reviewRepository.findAllByRecipeId(recipe.getId()).size(),
                            averageRating(reviewRepository.findAllByRecipeId(recipe.getId())),
                            new MainPageResponse.FindMember(recipe.getMember().getId(),
                                    recipe.getMember().getName())));
        }
        return recommendRecipes;
    }

    private List<MainPageResponse.RecipeReview> buildRecipeReviewList(List<Review> popularReviews) {
        List<MainPageResponse.RecipeReview> reviews = new ArrayList<>();
        for (Review review : popularReviews) {
            reviews.add(new MainPageResponse.RecipeReview(
                    review.getId(),
                    review.getContent(),
                    review.getRating(),
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
        mainPageResponse.plusReviews(reviews);
        mainPageResponse.plusRecipes(recommendRecipes);
        return mainPageResponse;
    }

    public MainPageResponse findMainPage(Member member) {
        int totalRecipeCount = recipeRepository.findAll().size();//총레시피 추가
        List<Member> popularRecipers = findRecipers(popularReciperCount);
        List<Recipe> recommendRecipes = findRecommendRecipes(member, recommendRecipeCount);
        List<Review> recipeReviews = findReviews(popularReviewCount);
        if (member != null) {
            Long joinDate = ChronoUnit.DAYS.between(member.getCreatedAt(), LocalDateTime.now());
            String profileMemberName = member.getName();
            return buildMainPageResponse(totalRecipeCount, joinDate, profileMemberName,
                    popularRecipers, recommendRecipes, recipeReviews);
        } else {
            return buildMainPageResponse(totalRecipeCount, null, null,
                    popularRecipers, recommendRecipes, recipeReviews);
        }

    }

    private List<Member> findRecipers(int reciperCount) {
        List<Member> members = memberRepository.findAll();
        Map<Float, Member> popularRecipers = new HashMap<>();
        for (Member member : members) {
            List<Recipe> recipes = recipeRepository.findAllByMemberId(member.getId());//멤버와 관련된 레시피 찾기
            List<Review> reviews = new ArrayList<>();//모든 리뷰를 저장할 리스트
            for (Recipe recipe : recipes) {
                List<Review> reviewOfRecipe = reviewRepository.findAllByRecipeId(recipe.getId());//레시피와 관련된 리뷰 찾기
                reviews.addAll(reviewOfRecipe);//여기에 저장
            }
            popularRecipers.put(averageRating(reviews), member);//점수와 관련된 멤버 다 추가
            reviews.clear();
        }
        List<Member> topRecipers = popularRecipers.entrySet()
                .stream()
                .sorted(Map.Entry.<Float, Member>comparingByKey().reversed())
                .limit(reciperCount)
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
        return topRecipers;
    }

    private List<Recipe> findRecommendRecipes(Member member, int recipeCount) {
        List<Onboard> memberOnboard = onboardRepository.findAllByMemberId(member.getId());
        Map<Category, List<Onboard>> classify = new HashMap<>();
        List<Recipe> recipes = new ArrayList<>();
        for (Onboard onboard : memberOnboard) {
            if (classify.containsKey(onboard.getCategory())) {
                classify.get(onboard.getCategory()).add(onboard);
            } else {
                List<Onboard> onboards = new ArrayList<>();
                onboards.add(onboard);
                classify.put(onboard.getCategory(), onboards);
            }
        }
        for (Category category : classify.keySet()) {
            List<Recipe> recipeList = recipeRepository.findAllByCategory(category);
            List<Onboard> onboards = classify.get(category);
            recipes.addAll(findBestRecipe(recipeList, onboards, recipeCount / classify.size()));
        }
        return recipes;
    }

    private List<Review> findReviews(int reviewCount) {
        List<Review> reviews = reviewRepository.findAll();
        Collections.shuffle(reviews);
        return reviews.size() > reviewCount ? reviews.subList(0, reviewCount) : reviews;
    }

    private List<Recipe> findBestRecipe(List<Recipe> recipes, List<Onboard> onboards, int recipeCount) {
        @Getter
        class BestRecipe {
            Recipe recipe;
            float averageRating;

            BestRecipe(Recipe recipe, float averageRating) {
                this.recipe = recipe;
                this.averageRating = averageRating;
            }
        }
        List<Recipe> recipeList = new ArrayList<>();
        Set<Recipe> recipeSet = new HashSet<>();
        List<BestRecipe> bestRecipe = new ArrayList<>();
        for (Recipe recipe : recipes) {
            if (findRecipeOfOnboard(recipe, onboards)) {
                recipeSet.add(recipe);
            }
        }
        for (Recipe recipe : recipeSet) {
            List<Review> reviews = reviewRepository.findAllByRecipeId(recipe.getId());
            bestRecipe.add(new BestRecipe(recipe, averageRating(reviews)));
        }
        Collections.sort(bestRecipe, (o1, o2) -> Float.compare(o2.averageRating, o1.averageRating));
        for (int i = 0; i < recipeCount; i++) {
            recipeList.add(bestRecipe.get(i).getRecipe());
        }
        return recipeList;
    }

    private float averageRating(List<Review> reviews) {
        float total = 0f;
        for (Review review : reviews) {
            total += review.getRating();
        }
        return total / (float) reviews.size();
    }

    private boolean findRecipeOfOnboard(Recipe recipe, List<Onboard> onboards) {
        List<Hashtag> hashtags = hashtagRepository.findAllByRecipeId(recipe.getId());
        for (Onboard onboard : onboards) {
            if (recipe.getTitle().contains(onboard.getKeyword())) {
                return true;
            } else if (recipe.getContent().contains(onboard.getKeyword())) {
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
}
