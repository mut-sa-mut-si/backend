package grwm.develop.recipe;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;
import grwm.develop.Category;
import grwm.develop.member.Member;
import grwm.develop.member.MemberRepository;
import grwm.develop.member.MemberService;
import grwm.develop.notification.RecipeNotification;
import grwm.develop.notification.RecipeNotificationRepository;
import grwm.develop.notification.Type;
import grwm.develop.recipe.dto.ReadLockRecipeResponse;
import grwm.develop.recipe.dto.ReadRecipeResponse;
import grwm.develop.recipe.dto.RecipeListResponse;
import grwm.develop.recipe.dto.WriteRecipeRequest;
import grwm.develop.recipe.dto.WriteReviewRequest;
import grwm.develop.recipe.hashtag.Hashtag;
import grwm.develop.recipe.hashtag.HashtagRepository;
import grwm.develop.recipe.image.Image;
import grwm.develop.recipe.image.ImageRepository;
import grwm.develop.recipe.review.Review;
import grwm.develop.recipe.review.ReviewRepository;
import grwm.develop.recipe.scrap.Scrap;
import grwm.develop.recipe.scrap.ScrapRepository;
import grwm.develop.subscribe.*;
import jakarta.persistence.EntityNotFoundException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

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
    private static final int RECIPE_PRICE = 120;

    private final MemberService memberService;

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
    private final BuyRecipeRepository buyRecipeRepository;
    private final RecipeNotificationRepository recipeNotificationRepository;

    @Transactional
    public void writeRecipe(Member member, WriteRecipeRequest request, List<MultipartFile> images) {
        Recipe recipe = buildRecipe(member, request);
        recipeRepository.save(recipe);

        List<Hashtag> hashtags = buildHashtags(request, recipe);
        hashtagRepository.saveAll(hashtags);

        List<Image> uploadedImages = getUploadedImages(images, recipe);
        imageRepository.saveAll(uploadedImages);

        SubscribeItem subscribeItem = subscribeItemRepository.findByMemberId(member.getId());
        List<Subscribe> subscribes = subscribeRepository.findBySubscribeItemId(subscribeItem.getId());
        subscribes.forEach(subscribe -> recipeNotificationRepository.save(buildRecipeNotification(subscribe)));
    }

    private RecipeNotification buildRecipeNotification(Subscribe subscribe) {
        Member subscribeMember = subscribe.getMember();
        return RecipeNotification.builder()
                .member(subscribeMember)
                .type(Type.RECIPE)
                .build();
    }

    @Transactional
    public void writeReview(Member member, WriteReviewRequest writeReviewRequest, Long id) {
        Recipe recipe = recipeRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        Review review = buildReview(member, writeReviewRequest, recipe);
        reviewRepository.save(review);

        RecipeNotification recipeNotification = buildReviewNotification(recipe);
        log.info("recipeNotification={}", recipeNotification);
        recipeNotificationRepository.save(recipeNotification);
    }

    private RecipeNotification buildReviewNotification(Recipe recipe) {
        return RecipeNotification.builder()
                .member(recipe.getMember())
                .recipe(recipe)
                .type(Type.REVIEW)
                .build();
    }

    @Transactional
    public void clickScrap(Member member, Long id) {
        if (!scrapRepository.existsByMemberIdAndRecipeId(member.getId(), id)) {
            Recipe recipe = recipeRepository.findById(id).orElseThrow(EntityNotFoundException::new);
            Scrap scrap = buildScrap(member, recipe);
            scrapRepository.save(scrap);
        }
    }

    @Transactional
    public void deleteScrap(Member member, Long id) {
        Optional<Scrap> scrap = scrapRepository.findByMemberIdAndRecipeId(member.getId(), id);
        scrap.ifPresent(scrapRepository::delete);
    }

    public ReadRecipeResponse buyRecipe(Member member, Long id) {
        Recipe recipe = recipeRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        buyRecipeRepository.save(buildbuyrecipe(member, recipe));
        member.setPoint(member.getPoint() - RECIPE_PRICE);
        return findRecipe(id, member);
    }

    private buyRecipe buildbuyrecipe(Member member, Recipe recipe) {
        return buyRecipe.builder().member(member).recipe(recipe).build();
    }

    private Recipe buildRecipe(Member member, WriteRecipeRequest request) {
        return Recipe.builder().category(getCategory(request.category())).title(request.title())
                .content(request.content()).isPublic(request.isPublic()).member(member).build();
    }

    private Category getCategory(String inputCategory) {
        if (inputCategory.equals("SKIN")) {
            return Category.SKIN;
        }
        if (inputCategory.equals("HEALTH")) {
            return Category.HEALTH;
        }
        return Category.NUTRIENTS;
    }

    private List<Hashtag> buildHashtags(WriteRecipeRequest request, Recipe recipe) {
        return request.hashtags().stream()
                .map(hashtag -> Hashtag.builder().content(hashtag.content()).recipe(recipe).build()).toList();
    }

    public Review buildReview(Member member, WriteReviewRequest writeReviewRequest, Recipe recipe) {
        return Review.builder().content(writeReviewRequest.content()).rating(writeReviewRequest.rating()).member(member)
                .recipe(recipe).build();
    }

    public Scrap buildScrap(Member member, Recipe recipe) {
        return Scrap.builder().member(member).recipe(recipe).build();
    }

    private List<Recipe> buildSearchRecipeList(String keyword) {
        List<Recipe> recipesContent = recipeRepository.findByContentContaining(keyword);
        List<Recipe> recipesTitle = recipeRepository.findByTitleContaining(keyword);
        List<Hashtag> hashtags = hashtagRepository.findByContentContaining(keyword);
        List<Recipe> recipesHashtag = new ArrayList<>();
        List<Recipe> recipesName = findRecipeContainingName(recipeRepository.findAll(), keyword);
        for (Hashtag hashtag : hashtags) {
            Recipe recipe = hashtag.getRecipe();
            recipesHashtag.add(recipe);
        }
        return integrateRecipe(recipesContent, recipesTitle, recipesHashtag, recipesName);
    }

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

    private List<Image> getUploadedImages(List<MultipartFile> images, Recipe recipe) {
        return images.stream().map(this::tryConvertFile).map(this::uploadImage)
                .map(uploadImageUrl -> Image.builder().url(uploadImageUrl).recipe(recipe).build()).toList();
    }

    private File tryConvertFile(MultipartFile image) {
        try {
            return convertFile(image).orElseThrow(IllegalArgumentException::new);
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
        amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, file));
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
            return ReadRecipeResponse.of(recipe.getId(), recipe.getTitle(), recipe.getContent(), recipeCount,
                    reviewCount, ratingAverage, false,true, writer, images, hashtags, reviews, recipe);
        } else {
            boolean isClickedScrap = scrapRepository.existsByMemberIdAndRecipeId(member.getId(), recipe.getId());
            return ReadRecipeResponse.of(recipe.getId(), recipe.getTitle(), recipe.getContent(), recipeCount,
                    reviewCount, ratingAverage, isClickedScrap,isWriting(member,recipe), writer, images, hashtags, reviews, recipe);
        }

    }

    public ReadLockRecipeResponse findRockRecipe(Member member, Long id) {
        Recipe recipe = recipeRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        Member writer = recipe.getMember();
        return ReadLockRecipeResponse.of(member.getPoint(), writer);
    }

    public RecipeListResponse findRecipeList(Member member, String category) {
        List<Recipe> recipes = recipeRepository.findAllByCategory(getCategoryEnum(category));
        RecipeListResponse recipeListResponse = buildRecipeList(recipes);
        if (member == null) {
            return recipeListResponse;
        } else {
            return isPublicList(recipeListResponse, member);
        }
    }

    public RecipeListResponse findDefaultRecipes(Member member) {
        List<Recipe> recipes = recipeRepository.findAll();
        RecipeListResponse recipeListResponse = buildRecipeList(recipes);
        if (member == null) {
            return recipeListResponse;
        } else {
            return isPublicList(recipeListResponse, member);
        }
    }

    public Category getCategoryEnum(String category) {
        if (category.equals("SKIN")) {
            return Category.SKIN;
        }
        if (category.equals("HEALTH")) {
            return Category.HEALTH;
        }
        return Category.NUTRIENTS;
    }

    public RecipeListResponse searchRecipeList(Member member, String keyword) {
        List<Recipe> recipes = buildSearchRecipeList(keyword);
        RecipeListResponse searchRecipe = buildRecipeList(recipes);
        searchRecipe.setKeyword(keyword);
        if (member == null) {
            return searchRecipe;
        } else {
            return isPublicList(searchRecipe, member);
        }

    }

    private boolean isSubscribe(Member member, Member writer) {
        SubscribeItem subscribeItem = subscribeItemRepository.findByMemberId(writer.getId());
        List<Subscribe> subscribes = subscribeRepository.findAllByMemberId(member.getId());
        for (Subscribe subscribe : subscribes) {
            if (subscribe.getSubscribeItem().getMember().getId().equals(subscribeItem.getMember().getId())) {
                return true;
            }
        }
        return false;
    }

    private RecipeListResponse isPublicList(RecipeListResponse recipeListResponse, Member member) {
        for (RecipeListResponse.FindRecipe findRecipe : recipeListResponse.getRecipes()) {
            Member writer = memberRepository.findById(findRecipe.getMember().getId())
                    .orElseThrow(EntityNotFoundException::new);
            if (!findRecipe.isPublic() && (isSubscribe(member, writer) || isBuyRecipe(findRecipe.getId(), member)||isMyRecipe(findRecipe.getId(), member.getId()))) {
                findRecipe.setPublic(true);
            }
        }
        return recipeListResponse;
    }

    private boolean isBuyRecipe(Long recipeId, Member member) {
        return buyRecipeRepository.existsByMemberIdAndRecipeId(member.getId(), recipeId);
    }


    public List<Recipe> integrateRecipe(List<Recipe> recipesContent, List<Recipe> recipesHashtag,
                                        List<Recipe> recipesTitle,
                                        List<Recipe> recipesName) {
        Set<Recipe> recipeSet = new LinkedHashSet<>();

        recipeSet.addAll(recipesContent);
        recipeSet.addAll(recipesHashtag);
        recipeSet.addAll(recipesTitle);
        recipeSet.addAll(recipesName);
        return new ArrayList<>(recipeSet);
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

    private List<Recipe> findRecipeContainingName(List<Recipe> recipes, String keyword) {
        List<Recipe> recipeList = new ArrayList<>();
        for (Recipe recipe : recipes) {
            if (recipe.getMember().getName().contains(keyword)) {
                recipeList.add(recipe);
            }
        }
        return recipeList;
    }
    private boolean isWriting(Member member, Recipe recipe)
    {
        if(reviewRepository.existsByRecipeIdAndMemberId(member.getId(), recipe.getId()))
        {
            return true;
        }
        else if(recipe.getMember().getId().equals(member.getId()))
        {
            return true;
        }else
        {
            return false;
        }
    }
    private boolean isMyRecipe(Long recipeId, Long memberId){
        if(recipeId.equals(memberId))
        {
            return true;
        }
        return false;
    }
}
