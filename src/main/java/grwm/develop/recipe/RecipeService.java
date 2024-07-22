package grwm.develop.recipe;


import grwm.develop.member.Member;
import grwm.develop.member.MemberRepository;
import grwm.develop.recipe.comment.Comment;
import grwm.develop.recipe.comment.CommentRepository;
import grwm.develop.recipe.comment.reply.Reply;
import grwm.develop.recipe.comment.reply.ReplyRepository;
import grwm.develop.recipe.dto.CreateCommentsPage;
import grwm.develop.recipe.dto.CreateDetailPage;
import grwm.develop.recipe.dto.CreateDetailPageLogin;
import grwm.develop.recipe.hashtag.Hashtag;
import grwm.develop.recipe.hashtag.HashtagRepository;
import grwm.develop.recipe.image.Image;
import grwm.develop.recipe.image.ImageRepository;
import grwm.develop.recipe.like.Like;
import grwm.develop.recipe.like.LikeRepository;
import grwm.develop.recipe.scrap.Scrap;
import grwm.develop.recipe.scrap.ScrapRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class RecipeService {

    private final ReplyRepository replyRepository;

    private final RecipeRepository recipeRepository;

    private final LikeRepository likeRepository;

    private final ScrapRepository scrapRepository;

    private final CommentRepository commentRepository;

    private final ImageRepository imageRepository;

    private final HashtagRepository hashtagRepository;

    private final MemberRepository memberRepository;

    public CreateDetailPage createDetailPage(Long id) {
        Recipe recipe = recipeRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        Member writer = recipe.getMember(); // 작성자를 가져온다
        int likes = likeRepository.findByRecipe(recipe).size(); // 좋아요 수
        int comments = commentRepository.findByRecipe(recipe).size(); // 댓글 수
        int scraps = scrapRepository.findByRecipe(recipe).size(); // 스크랩 수
        List<Image> images = imageRepository.findByRecipe(recipe);
        if (images == null || images.isEmpty()) {
            throw new EntityNotFoundException();
        }
        List<Hashtag> hashtags = hashtagRepository.findByRecipe(recipe);
        if (hashtags == null || hashtags.isEmpty()) {
            throw new EntityNotFoundException();
        }

        return CreateDetailPage.of(recipe.getId(), recipe.getTitle(), recipe.getContent(), writer, likes, comments, scraps, recipe.getCreatedAt(), images, hashtags);
    }

    public CreateDetailPageLogin createDetailPageLogin(Long id, String email) {
        Recipe recipe = recipeRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        Member writer = recipe.getMember(); // 작성자를 가져온다
        Member member = memberRepository.findByEmail(email).orElseThrow(EntityNotFoundException::new);
        int likes = likeRepository.findByRecipe(recipe).size(); // 좋아요 수
        int comments = commentRepository.findByRecipe(recipe).size(); // 댓글 수
        int scraps = scrapRepository.findByRecipe(recipe).size(); // 스크랩 수
        boolean isClickedLike = isLiked(member, id);
        boolean isClickedScrap = isScraped(member, id);
        List<Image> images = imageRepository.findByRecipe(recipe);
        if (images == null || images.isEmpty()) {
            throw new EntityNotFoundException();
        }
        List<Hashtag> hashtags = hashtagRepository.findByRecipe(recipe);
        if (hashtags == null || hashtags.isEmpty()) {
            throw new EntityNotFoundException();
        }

        return CreateDetailPageLogin.of(recipe.getId(), recipe.getTitle(), recipe.getContent(), writer, likes, comments, scraps, recipe.getCreatedAt(), isClickedLike, isClickedScrap, images, hashtags);
    }

    public List<CreateCommentsPage> createCommentsPage(Long id) {
        Recipe recipe = recipeRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        List<Comment> comments = commentRepository.findByRecipe(recipe);
        List<CreateCommentsPage> createCommentsPages = new ArrayList<>();
        for (Comment comment : comments) {
            List<Reply> replies = replyRepository.findByComment(comment);
            createCommentsPages.add(CreateCommentsPage.of(comment, replies));
        }
        return createCommentsPages;
    }

    public Member MemberByEmail(String email) {
        return memberRepository.findByEmail(email).orElseThrow(EntityNotFoundException::new);
    }

    public int createLike(Member member, Long id) {
        Recipe recipe = recipeRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        Like like = new Like(member, recipe);
        likeRepository.save(like);
        int likeCount = LikeCount(recipe);
        return likeCount;
    }

    public int createScrap(Member member, Long id) {
        Recipe recipe = recipeRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        Scrap scrap = new Scrap(member, recipe);
        scrapRepository.save(scrap);
        int scrapCount = ScrapCount(recipe);
        return scrapCount;
    }

    public int LikeCount(Recipe recipe) {
        return likeRepository.findByRecipe(recipe).size();
    }

    public int ScrapCount(Recipe recipe) {
        return scrapRepository.findByRecipe(recipe).size();
    }

    public boolean isLiked(Member member, Long id) {
        Recipe recipe = recipeRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        List<Like> likes = likeRepository.findByRecipe(recipe);
        for (Like like : likes) {
            if (like.getMember().equals(member)) {
                return true;
            }
        }
        return false;
    }

    public boolean isScraped(Member member, Long id) {
        Recipe recipe = recipeRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        List<Scrap> scraps = scrapRepository.findByRecipe(recipe);
        for (Scrap scrap : scraps) {
            if (scrap.getMember().equals(member)) {
                return true;
            }
        }
        return false;
    }
}

