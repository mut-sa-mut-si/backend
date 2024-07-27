package grwm.develop.chat.dto;

import grwm.develop.chat.room.Room;
import grwm.develop.member.Member;
import java.util.List;
import java.util.Map;

public record FindAllChatRoomsResponse(List<ChatRoomDTO> chats) {

    public static FindAllChatRoomsResponse of(List<Room> rooms, Map<Long, String> messageMap,
                                              Map<Long, Member> memberMap) {

        return new FindAllChatRoomsResponse(rooms.stream()
                .map(room ->
                        new ChatRoomDTO(
                                room.getId(),
                                messageMap.get(room.getId()),
                                new MemberDTO(
                                        memberMap.get(room.getId()).getId(),
                                        memberMap.get(room.getId()).getName())))
                .toList());
    }

    public record ChatRoomDTO(Long id, String lastMessage, MemberDTO member) {
    }

    public record MemberDTO(Long id, String name) {
    }
}
