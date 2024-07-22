package grwm.develop.recipe.comment.reply;

import grwm.develop.auth.security.UserDetailsImpl;
import grwm.develop.recipe.comment.reply.dto.WriteReplyRequest;
import grwm.develop.recipe.comment.reply.dto.ReplyResponse;
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
public class ReplyController {

    private final ReplyService replyService;

    @PostMapping("{recipeId}/comments/{commentId}")
    public ResponseEntity<ReplyResponse> create(@PathVariable Long commentId,
                                                @AuthenticationPrincipal UserDetailsImpl userDetails,
                                                @RequestBody WriteReplyRequest request) {
        ReplyResponse response = replyService.writeReply(commentId, userDetails.member(), request);
        return ResponseEntity.ok(response);
    }
}
