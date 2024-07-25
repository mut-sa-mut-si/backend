package grwm.develop.chat;

import grwm.develop.auth.security.UserDetailsImpl;
import grwm.develop.chat.dto.SendChatDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChatSocketHandler {

    private final ChatService chatService;

    @MessageMapping("/{roomId}/send")
    @SendTo("/chats/{roomId}")
    public ResponseEntity<SendChatDTO> chat(@RequestBody SendChatDTO chat,
                                            @DestinationVariable Long roomId,
                                            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        chatService.saveChat(roomId, chat, userDetails.member());
        return ResponseEntity.ok().body(chat);
    }
}
