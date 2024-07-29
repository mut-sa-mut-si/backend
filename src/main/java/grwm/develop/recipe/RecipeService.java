package grwm.develop.recipe;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;
import grwm.develop.Category;
import grwm.develop.member.Member;
import grwm.develop.member.MemberRepository;
import grwm.develop.recipe.dto.*;
import grwm.develop.recipe.hashtag.Hashtag;
import grwm.develop.recipe.hashtag.HashtagRepository;
import grwm.develop.recipe.image.Image;
import grwm.develop.recipe.image.ImageRepository;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

import grwm.develop.recipe.review.Review;
import grwm.develop.recipe.review.ReviewRepository;
import grwm.develop.recipe.scrap.ScrapRepository;
import grwm.develop.subscribe.Subscribe;
import grwm.develop.subscribe.SubscribeItem;
import grwm.develop.subscribe.SubscribeItemRepository;
import grwm.develop.subscribe.SubscribeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecipeService {

    private static final String IMAGE_SAVE_PATH_PREFIX = "images/";

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final AmazonS3Client amazonS3Client;
    private final ImageRepository imageRepository;
    private final RecipeRepository recipeRepository;
    private final HashtagRepository hashtagRepository;
    private final ReviewRepository reviewRepository;
    private final MemberRepository memberRepository;
    private final SubscribeRepository subscribeRepository;
    private final SubscribeItemRepository subscribeItemRepository;
    private final ScrapRepository scrapRepository;

    @Transactional
    public void writeRecipe(Member member, WriteRecipeRequest request, List<MultipartFile> images) {
        Recipe recipe = buildRecipe(member, request);
        recipeRepository.save(recipe);

        List<Hashtag> hashtags = buildHashtags(request, recipe);
        hashtagRepository.saveAll(hashtags);

        List<Image> uploadedImages = getUploadedImages(images, recipe);
        imageRepository.saveAll(uploadedImages);
    }

    @Transactional
    public void writeReview(Member member, WriteReviewRequest writeReviewRequest, Long id) {
        Recipe recipe = recipeRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        Review review = buildReview(member, writeReviewRequest, recipe);
        reviewRepository.save(review);
    }

    private Recipe buildRecipe(Member member, WriteRecipeRequest request) {
        return Recipe.builder()
                .category(getCategory(request.category()))
                .title(request.title())
                .content(request.content())
                .isPublic(request.isPublic())
                .member(member)
                .build();
    }

    private Category getCategory(String inputCategory) {
        if (inputCategory.equals("피부미용")) {
            return Category.SKIN;
        }
        if (inputCategory.equals("헬스")) {
            return Category.HEALTH;
        }
        return Category.NUTRIENTS;
    }

    private List<Hashtag> buildHashtags(WriteRecipeRequest request, Recipe recipe) {
        return request.hashtags().stream()
                .map(hashtag ->
                        Hashtag.builder()
                                .content(hashtag.content())
                                .recipe(recipe)
                                .build()
                )
                .toList();
    }

    public Review buildReview(Member member, WriteReviewRequest writeReviewRequest, Recipe recipe) {
        return Review.builder()
                .content(writeReviewRequest.content())
                .rating(writeReviewRequest.rating())
                .member(member)
                .recipe(recipe)
                .build();
    }

    private List<Recipe> buildSearchRecipeList(String keyword) {
        List<Recipe> recipesContent = recipeRepository.findByContentContaining(keyword);
        List<Recipe> recipesTitle = recipeRepository.findByTitleContaining(keyword);
        List<Hashtag> hashtags = hashtagRepository.findByContentContaining(keyword);
        List<Recipe> recipesHashtag = new ArrayList<>();
        for (Hashtag hashtag : hashtags) {
            Recipe recipe = hashtag.getRecipe();
            recipesHashtag.add(recipe);
        }
        List<Recipe> recipes = integrateRecipe(recipesContent, recipesTitle, recipesHashtag);
        return recipes;
    }

    private RecipeListResponse buildRecipeList(List<Recipe> recipes) {
        RecipeListResponse recipeListResponse = new RecipeListResponse();
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
            recipeListResponse.plus(findRecipe);
        }
        return recipeListResponse;
    }

    private List<Image> getUploadedImages(List<MultipartFile> images, Recipe recipe) {
        return images.stream()
                .map(this::tryConvertFile)
                .map(this::uploadImage)
                .map(uploadImageUrl -> Image.builder()
                        .url(uploadImageUrl)
                        .recipe(recipe)
                        .build())
                .toList();
    }

    private File tryConvertFile(MultipartFile image) {
        try {
            return convertFile(image)
                    .orElseThrow(IllegalArgumentException::new);
        } catch (IOException e) {
            log.error("Do not converted multipart file to image.");
            throw new IllegalArgumentException("Do not converted multipart file to image.");
        }
    }

    private Optional<File> convertFile(MultipartFile image) throws IOException {
        File file = new File(Objects.requireNonNull(image.getOriginalFilename()));
        if (file.createNewFile()) {
            try (FileOutputStream outputStream = new FileOutputStream(file)) {
                outputStream.write(image.getBytes());
            }
            return Optional.of(file);
        }
        return Optional.empty();
    }

    private String uploadImage(File file) {
        String fileName = IMAGE_SAVE_PATH_PREFIX + file.getName();
        amazonS3Client.putObject(
                new PutObjectRequest(bucket, fileName, file)
        );
        return amazonS3Client.getUrl(bucket, fileName).toString();
    }

    public ReadRecipeResponse findRecipe(Long id, Member member) {
        Recipe recipe = recipeRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        Member writer = recipe.getMember();
        List<Review> reviews = reviewRepository.findAllByRecipeId(recipe.getId());
        List<Image> images = imageRepository.findAllByRecipeId(recipe.getId());
        List<Hashtag> hashtags = hashtagRepository.findAllByRecipeId(recipe.getId());
        int recipeCount = recipeRepository.findAllByMemberId(writer.getId()).size();
        int reviewCount = reviews.size();
        float ratingAverage = averageRating(reviews);
        if (member == null) {
            return ReadRecipeResponse.of(recipe.getId(), recipe.getTitle(), recipe.getContent(), recipeCount, reviewCount, ratingAverage, Optional.empty(), writer, images, hashtags, reviews);
        } else {
            Optional<Boolean> isClickedScrap = Optional.of(scrapRepository.existsByMemberIdAndRecipeId(member.getId(), recipe.getId()));
            return ReadRecipeResponse.of(recipe.getId(), recipe.getTitle(), recipe.getContent(), recipeCount, reviewCount, ratingAverage, isClickedScrap, writer, images, hashtags, reviews);
        }

    }

    public ReadLockRecipeResponse findRockRecipe(Member member, Long id) {
        Recipe recipe = recipeRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        Member writer = recipe.getMember();
        return ReadLockRecipeResponse.of(member.getPoint(), writer);
    }

    public RecipeListResponse findRecipeList(Member member, String category) {
        List<Recipe> recipes = recipeRepository.findAllByCategory(category);
        RecipeListResponse recipeListResponse = buildRecipeList(recipes);
        if (member == null) {
            return recipeListResponse;
        } else {
            return isSubscribeList(recipeListResponse, member);
        }
    }

    public RecipeListResponse searchRecipeList(Member member, String keyword) {
        List<Recipe> recipes = buildSearchRecipeList(keyword);
        RecipeListResponse searchRecipe = buildRecipeList(recipes);
        searchRecipe.setKeyword(keyword);
        if (member == null) {
            return searchRecipe;
        } else {
            return isSubscribeList(searchRecipe, member);
        }

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

    private RecipeListResponse isSubscribeList(RecipeListResponse recipeListResponse, Member member) {
        for (RecipeListResponse.FindRecipe findRecipe : recipeListResponse.getRecipes()) {
            Member writer = memberRepository.findById(findRecipe.getMember().getId()).
                    orElseThrow(EntityNotFoundException::new);
            if (findRecipe.isPublic() &&
                    isSubscribe(member, writer)) {
                findRecipe.setPublic(true);
            }
        }
        return recipeListResponse;
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

    private float averageRating(List<Review> reviews) {
        float total = 0f;
        for (Review review : reviews) {
            total += review.getRating();
        }
        return total / (float) reviews.size();
    }
}
