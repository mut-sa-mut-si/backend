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
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

public class MainPageService {

    @Getter
    @AllArgsConstructor
    public class Pair<K, V> {
        private K key;
        private V value;
    }

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
            recommendRecipes.add(
                    new MainPageResponse.RecommendRecipe(
                            recipe.getId(),
                            recipe.getTitle(),
                            imageRepository.findByRecipeId(recipe.getId()).getUrl(),
                            reviewRepository.findAllByRecipeId(recipe.getId()).size(),
                            averageRatingOfReview(reviewRepository.findAllByRecipeId(recipe.getId())),
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
        mainPageResponse.plusRecipes(recommendRecipes);
        mainPageResponse.plusReviews(reviews);
        return mainPageResponse;
    }

    public MainPageResponse findMainPage(Member member) {
        int totalRecipeCount = recipeRepository.findAll().size();
        List<Member> popularRecipers = findPopularRecipers(popularReciperCount);
        List<Recipe> recommendRecipes = findRecommendRecipes(member, recommendRecipeCount);
        List<Review> recipeReviews = findReviews(popularReviewCount);
        Long joinDate = member != null ? ChronoUnit.DAYS.between(member.getCreatedAt(), LocalDateTime.now()) : null;
        String profileMemberName = member != null ? member.getName() : null;
        return buildMainPageResponse(totalRecipeCount, joinDate, profileMemberName,
                popularRecipers, recommendRecipes, recipeReviews);
    }

    private List<Member> findPopularRecipers(int reciperCount) {
        List<Member> members = memberRepository.findAll();
        return bestRecipers(members).stream().limit(reciperCount).collect(Collectors.toList());
    }

    private List<Recipe> findRecommendRecipes(Member member, int recipeCount) {
        List<Recipe> recipes = new ArrayList<>();
        if (member == null) {//만약에 로그인을 안했던 유저라면 랜덤으로 6개 반환하기
            return findRandomValueList(recipeRepository.findAll(), recipeCount);
        }
        Map<Category, List<Onboard>> memberOnboard = classifyOnboard(member);
        for (Category category : memberOnboard.keySet()) {
            List<Recipe> recipeList = recipeRepository.findAllByCategory(category);
            List<Onboard> onboards = memberOnboard.get(category);
            recipes.addAll(bestRecipe(recipeList, onboards, recipeCount / memberOnboard.size()));
        }
        return recipes;
    }

    private List<Review> findReviews(int reviewCount) {
        List<Review> reviews = reviewRepository.findAll();
        return findRandomValueList(reviews, reviewCount);
    }

    private <T> List<T> findRandomValueList(List<T> list, int Count) {
        Collections.shuffle(list);
        return list.size() > Count ? list.subList(0, Count) : list;
    }

    private Map<Category, List<Onboard>> classifyOnboard(Member member) {
        List<Onboard> memberOnboard = onboardRepository.findAllByMemberId(member.getId());
        Map<Category, List<Onboard>> classify = new HashMap<>();
        for (Onboard onboard : memberOnboard) {
            if (classify.containsKey(onboard.getCategory().toString())) {
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
                bestRecipes.add(new Pair<>(averageRatingOfReview(reviewRepository.findAllByRecipeId(recipe.getId())), recipe));
            }
        }
        Collections.sort(bestRecipes, Comparator.comparing(Pair::getKey));
        return bestRecipes.stream().limit(recipeCount).map(Pair::getValue).collect(Collectors.toList());
    }

    private List<Member> bestRecipers(List<Member> members) {

        List<Pair<Float, Member>> memberList = new ArrayList<>();
        for (Member member : members) {
            List<Recipe> recipes = recipeRepository.findAllByMemberId(member.getId());//멤버와 관련된 레시피 찾기
            List<Review> reviews = new ArrayList<>();//모든 리뷰를 저장할 리스트
            for (Recipe recipe : recipes) {
                List<Review> reviewsOfRecipe = reviewRepository.findAllByRecipeId(recipe.getId());
                reviews.addAll(reviewsOfRecipe);
            }
            memberList.add(new Pair<>(averageRatingOfReview(reviews), member));
        }
        Collections.sort(memberList, Comparator.comparing(Pair::getKey));
        return memberList.stream().map(Pair::getValue).collect(Collectors.toList());
    }

    private float averageRatingOfReview(List<Review> reviews) {
        if (reviews.isEmpty()) return 0;
        float total = 0f;
        for (Review review : reviews) {
            total += review.getRating();
        }
        return total / (float) reviews.size();
    }

    private boolean findRecipeOfOnboard(Recipe recipe, List<Onboard> onboards) {
        List<Hashtag> hashtags = hashtagRepository.findAllByRecipeId(recipe.getId());
        for (Onboard onboard : onboards) {
            if (recipe.getTitle().contains(onboard.getKeyword().toString())) {
                return true;
            } else if (recipe.getContent().contains(onboard.getKeyword().toString())) {
                return true;
            }
            for (Hashtag hashtag : hashtags) {
                if (hashtag.getContent().contains(onboard.getKeyword().toString())) {
                    return true;
                }
            }
        }
        return false;
    }
}
