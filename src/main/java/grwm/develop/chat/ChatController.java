package grwm.develop.chat;

import grwm.develop.auth.security.UserDetailsImpl;
import grwm.develop.chat.dto.FindAllChatRoomsResponse;
import grwm.develop.chat.dto.FindChatRoomResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/chats")
public class ChatController {

    private final ChatService chatService;

    @GetMapping
    public ResponseEntity<FindAllChatRoomsResponse> findAll(
            @RequestParam(name = "category", required = false) String category,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        FindAllChatRoomsResponse response = chatService.findAllChats(category, userDetails.member());
        log.info("response={}", response);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FindChatRoomResponse> find(@PathVariable(name = "id") Long id,
                                                     @AuthenticationPrincipal UserDetailsImpl userDetails) {

        FindChatRoomResponse response = chatService.findChat(id, userDetails.member());
        return ResponseEntity.ok().body(response);
    }

    @PostMapping
    public ResponseEntity<String> participate(@RequestParam(name = "memberId") Long memberId,
                                              @RequestParam(name = "category", required = false) String category,
                                              @AuthenticationPrincipal UserDetailsImpl userDetails) {

        chatService.participateChat(memberId, userDetails.member(), category);
        return ResponseEntity.ok().body("ok");
    }
}
