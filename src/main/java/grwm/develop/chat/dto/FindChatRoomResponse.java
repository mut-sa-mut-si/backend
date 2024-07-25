package grwm.develop.chat.dto;

import grwm.develop.chat.Chat;
import grwm.develop.member.Member;
import java.util.ArrayList;
import java.util.List;


public record FindChatRoomResponse(MemberDTO member, List<ChatDTO> chats) {

    public static FindChatRoomResponse of(Member member, List<Chat> myChats, List<Chat> otherChats) {
        List<ChatDTO> totalChats = new ArrayList<>();
        addMyChats(myChats, totalChats);
        addOtherChats(otherChats, totalChats);
        totalChats.sort((chat1, chat2) -> chat2.createdAt.compareTo(chat1.createdAt));
        return new FindChatRoomResponse(
                new MemberDTO(member.getId(), member.getName()), totalChats);
    }

    private static void addMyChats(List<Chat> myChats, List<ChatDTO> totalChats) {
        myChats.forEach(myChat -> totalChats.add(
                new ChatDTO(
                        myChat.getId(),
                        myChat.getContent(),
                        "me",
                        myChat.getCreatedAt().toString())));
    }

    private static void addOtherChats(List<Chat> otherChats, List<ChatDTO> totalChats) {
        otherChats.forEach(otherChat -> totalChats.add(
                new ChatDTO(
                        otherChat.getId(),
                        otherChat.getContent(),
                        "other",
                        otherChat.getCreatedAt().toString())));
    }

    public record MemberDTO(Long id, String name) {
    }

    public record ChatDTO(Long id, String message, String owner, String createdAt) {
    }
}
