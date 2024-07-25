package grwm.develop.chat;

import grwm.develop.chat.dto.FindAllChatRoomsResponse;
import grwm.develop.chat.dto.FindChatRoomResponse;
import grwm.develop.chat.participant.Participant;
import grwm.develop.chat.participant.ParticipantRepository;
import grwm.develop.chat.room.Room;
import grwm.develop.member.Member;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatService {

    private final ChatRepository chatRepository;
    private final ParticipantRepository participantRepository;

    public FindAllChatRoomsResponse findAllChats(String category, Member member) {
        List<Participant> participants = participantRepository.findByMemberId(member.getId());

        Map<Long, String> messageMap = new HashMap<>();
        Map<Long, Member> memberMap = new HashMap<>();

        List<Room> rooms = getRooms(category, member, participants, messageMap, memberMap);
        return FindAllChatRoomsResponse.of(rooms, messageMap, memberMap);
    }

    private List<Room> getRooms(String category,
                                Member member,
                                List<Participant> participants,
                                Map<Long, String> messageMap,
                                Map<Long, Member> memberMap) {

        return participants.stream()
                .map(Participant::getRoom)
                .filter(room -> room.getCategory().toString().equals(category))
                .distinct()
                .peek(room -> mappedLastMessage(room, messageMap))
                .peek(room -> mappedOppositeMember(room, member, memberMap, participants))
                .toList();
    }

    private void mappedLastMessage(Room room, Map<Long, String> messageMap) {
        List<Chat> chats = chatRepository.findByRoomId(room.getId());
        if (!chats.isEmpty()) {
            messageMap.put(room.getId(), chats.get(chats.size() - 1).getContent());
        }
    }

    private void mappedOppositeMember(Room room,
                                      Member member,
                                      Map<Long, Member> memberMap,
                                      List<Participant> participants) {

        Member oppositeMember = participants.stream()
                .map(Participant::getMember)
                .filter(participantMember -> !participantMember.equals(member))
                .findFirst()
                .orElse(null);
        memberMap.put(room.getId(), oppositeMember);
    }

    public FindChatRoomResponse findChat(Long roomId, Member member) {
        List<Chat> chats = chatRepository.findByRoomId(roomId);

        List<Participant> participants = participantRepository.findByRoomId(roomId);
        Member other = getOtherMember(member, participants);

        List<Chat> myChats = getMyChats(member, chats);
        List<Chat> otherChats = getOtherChats(member, chats);
        return FindChatRoomResponse.of(
                Objects.requireNonNull(other), myChats, otherChats);
    }

    private Member getOtherMember(Member member, List<Participant> participants) {
        return participants.stream()
                .filter(participant -> !participant.getMember().equals(member))
                .findFirst()
                .map(Participant::getMember)
                .orElse(null);
    }

    private List<Chat> getMyChats(Member member, List<Chat> chats) {
        return chats.stream()
                .filter(chat -> chat.getMember().equals(member))
                .toList();
    }

    private List<Chat> getOtherChats(Member member, List<Chat> chats) {
        return chats.stream()
                .filter(chat -> !chat.getMember().equals(member))
                .toList();
    }
}
