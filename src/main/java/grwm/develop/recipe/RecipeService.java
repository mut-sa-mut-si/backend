package grwm.develop.recipe;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import grwm.develop.member.Member;
import grwm.develop.recipe.dto.WriteRecipeRequest;
import grwm.develop.recipe.hashtag.Hashtag;
import grwm.develop.recipe.hashtag.HashtagRepository;
import grwm.develop.recipe.image.Image;
import grwm.develop.recipe.image.ImageRepository;
import grwm.develop.utils.S3Properties;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecipeService {

    private static final String BUCKET = "bucket";
    private static final String IMAGE_SAVE_PATH_PREFIX = "images/";
    
    private final S3Properties s3Properties;
    private final AmazonS3Client amazonS3Client;
    private final ImageRepository imageRepository;
    private final RecipeRepository recipeRepository;
    private final HashtagRepository hashtagRepository;

    @Transactional
    public void writeRecipe(Member member, WriteRecipeRequest request, List<MultipartFile> images) {
        Recipe recipe = buildRecipe(member, request);
        recipeRepository.save(recipe);

        List<Hashtag> hashtags = buildHashtags(request, recipe);
        hashtagRepository.saveAll(hashtags);

        List<Image> uploadedImages = getUploadedImages(images, recipe);
        imageRepository.saveAll(uploadedImages);
    }

    private Recipe buildRecipe(Member member, WriteRecipeRequest request) {
        return Recipe.builder()
                .category(
                        Category.valueOf(request.category())
                )
                .title(request.title())
                .content(request.content())
                .isPublic(request.isPublic())
                .member(member)
                .build();
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
        String bucket = s3Properties.getS3().get(BUCKET);
        String fileName = IMAGE_SAVE_PATH_PREFIX + file.getName();
        amazonS3Client.putObject(
                new PutObjectRequest(bucket, fileName, file)
                        .withCannedAcl(CannedAccessControlList.PublicRead)
        );
        return amazonS3Client.getUrl(bucket, fileName).toString();
    }
}
