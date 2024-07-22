package grwm.develop.recipe.comment;

import grwm.develop.auth.security.UserDetailsImpl;
import grwm.develop.recipe.comment.dto.CommentResponse;
import grwm.develop.recipe.comment.dto.WriteCommentRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/recipes")
public class CommentController {

    private final CommentService commentService;

    @PostMapping("{id}/comments")
    public ResponseEntity<CommentResponse> CreateCommentResponse(@PathVariable Long id,
                                                                 @AuthenticationPrincipal UserDetailsImpl userDetails,
                                                                 @RequestBody WriteCommentRequest request) {
        CommentResponse response = commentService.writeComment(id, userDetails.member(), request);
        return ResponseEntity.ok(response);
    }
}
