package grwm.develop.chat;

import grwm.develop.chat.dto.SendChatDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ChatSocketHandler {

    private final ChatService chatService;

    @MessageMapping("/chats/{roomId}/send")
    @SendTo("/chats/{roomId}")
    public ResponseEntity<SendChatDTO> chat(@RequestBody SendChatDTO chat,
                                            @DestinationVariable Long roomId) {

        chatService.saveChat(roomId, chat, chat.memberId());
        return ResponseEntity.ok().body(chat);
    }
}
