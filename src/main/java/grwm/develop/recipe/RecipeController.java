package grwm.develop.recipe;

import grwm.develop.auth.jwt.JwtService;
import grwm.develop.member.Member;
import grwm.develop.recipe.dto.CreateCommentsPage;
import grwm.develop.recipe.dto.CreateDetailPage;
import grwm.develop.recipe.dto.CreateDetailPageLogin;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/receipes")
@RequiredArgsConstructor
public class RecipeController {

    private final RecipeService recipeService;

    private final JwtService jwtService;


    @GetMapping("/{id}/unauthentication")
    public ResponseEntity<CreateDetailPage> detail(@PathVariable("id") Long id) {
        CreateDetailPage createDetailPage = recipeService.createDetailPage(id);
        return ResponseEntity.ok().body(createDetailPage);
    }

    @GetMapping("/{id}/authentication")
    public ResponseEntity<CreateDetailPageLogin> detail(@PathVariable("id") Long id, @RequestHeader("Authorization") String token) {
        String member_email = jwtService.parse(token);
        CreateDetailPageLogin createDetailPageLogin = recipeService.createDetailPageLogin(id,member_email);
        return ResponseEntity.ok(createDetailPageLogin);
    }

    @GetMapping("/{id}/comments")
    public ResponseEntity<List<CreateCommentsPage>> ClickComment(@PathVariable("id") Long id, @RequestHeader("Authorization") String token) {
        List<CreateCommentsPage> comments = recipeService.createCommentsPage(id);
        return ResponseEntity.ok(comments);
    }

    @PatchMapping("/{id}/like")
    public ResponseEntity<Integer> ClickLike(@PathVariable("id") Long id, @RequestHeader("Authorization") String token) {
        String member_email = jwtService.parse(token);
        Member member = recipeService.MemberByEmail(member_email);
        int likeCount = recipeService.createLike(member, id);
        return ResponseEntity.ok(likeCount);
    }

    @PatchMapping("/scrap")
    public ResponseEntity<Integer> ClickScrap(@PathVariable("id") Long id, @RequestHeader("Authorization") String token) {
        String member_email = jwtService.parse(token);
        Member member = recipeService.MemberByEmail(member_email);
        int scrapCount = recipeService.createScrap(member, id);
        return ResponseEntity.ok(scrapCount);
    }

}
