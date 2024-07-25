package grwm.develop.recipe.dto;

import grwm.develop.member.Member;
import grwm.develop.recipe.Recipe;
import grwm.develop.recipe.hashtag.Hashtag;
import grwm.develop.recipe.image.Image;
import grwm.develop.recipe.review.Review;

import java.util.List;

public record ReadRecipeRequestLogin(Long id,String title, String content, int recipeCount, int reviewCount, float ratingAverage,
                                boolean isClickedScrap,
                                FindMember member,
                                List<FindImage> images,
                                List<FindHashtag> hashtags,
                                List<FindReview> reviews) {
    public static ReadRecipeRequestLogin of(Long id, String title, String content, int recipeCount, int reviewCount, float ratingAverage,
                                            boolean isClickedScrap,
                                            Member member,
                                       List<Image> images,
                                       List<Hashtag> hashtags,
                                       List<Review> reviews)
    {
        return new ReadRecipeRequestLogin(id,title,content,
                recipeCount,
                reviewCount,
                ratingAverage,
                isClickedScrap,
                new FindMember(member.getId(), member.getName()),
                images.stream().map(image ->
                        new FindImage(
                                image.getId(),
                                image.getUrl())).toList(),
                hashtags.stream().map(hashtag ->
                        new FindHashtag(
                                hashtag.getId(),
                                hashtag.getContent())).toList(),
                reviews.stream().map(review ->
                        new FindReview(
                                review.getId(),
                                review.getContent(),
                                review.getRating(),
                                new FindMember(review.getMember().getId(),review.getMember().getName()))).toList()
        );
    }
    record FindMember(Long id, String name){

    }
    record FindImage(Long id, String src){

    }
    record FindHashtag(Long id, String content){

    }
    record FindReview(Long id,String content, float rating, FindMember member)
    {

    }
}