package grwm.develop.recipe.dto;


import grwm.develop.member.Member;
import grwm.develop.recipe.hashtag.Hashtag;
import grwm.develop.recipe.image.Image;

import java.time.LocalDateTime;
import java.util.List;

public record CreateDetailPage(Long id,
                               String title,
                               String content,
                               FindMember member,
                               int likeCount,
                               int commentCount,
                               int scrapCount,
                               LocalDateTime createdAt,
                               List<FindImages> images,
                               List<FindHashtages> hashtags
) {
    public static CreateDetailPage of(Long id,
                                      String title,
                                      String content,
                                      Member member,
                                      int likeCount,
                                      int commentCount,
                                      int scrapCount,
                                      LocalDateTime createdAt,
                                      List<Image> images,
                                      List<Hashtag> hashtags) {
        return new CreateDetailPage(
                id,title,content,
                new FindMember(member.getId(), member.getName(), "기본이미지"),
                likeCount,
                commentCount,
                scrapCount,
                createdAt,
                images.stream().map(image ->
                        new FindImages(
                                image.getId(),
                                image.getUrl())).toList(),
                hashtags.stream().map(hashtag ->
                        new FindHashtages(
                                hashtag.getId(),
                                hashtag.getContent())).toList());
    }

    public record FindRecipe(Long id, String title, String content) {

    }

    public record FindMember(Long id, String name, String image) {

    }

    public record FindImages(Long id, String src) {

    }

    public record FindHashtages(Long id, String content) {

    }
}
