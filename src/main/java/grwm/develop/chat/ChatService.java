package grwm.develop.chat;

import grwm.develop.Category;
import grwm.develop.chat.dto.FindAllChatRoomsResponse;
import grwm.develop.chat.dto.FindChatRoomResponse;
import grwm.develop.chat.dto.SendChatDTO;
import grwm.develop.chat.participant.Participant;
import grwm.develop.chat.participant.ParticipantRepository;
import grwm.develop.chat.room.Room;
import grwm.develop.chat.room.RoomRepository;
import grwm.develop.member.Member;
import grwm.develop.member.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
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

    private final RoomRepository roomRepository;
    private final ChatRepository chatRepository;
    private final MemberRepository memberRepository;
    private final ParticipantRepository participantRepository;

    public FindAllChatRoomsResponse findAllChats(String category, Member member) {
        List<Participant> participants = participantRepository.findByMemberId(member.getId());
        List<Room> rooms = getRooms(category, participants);

        Map<Long, String> messageMap = new HashMap<>();
        Map<Long, Member> memberMap = new HashMap<>();
        setMessageAndMemberMap(member, rooms, messageMap, memberMap);
        return FindAllChatRoomsResponse.of(rooms, messageMap, memberMap);
    }

    private List<Room> getRooms(String category, List<Participant> participants) {
        return participants.stream()
                .map(Participant::getRoom)
                .filter(room ->
                        room.getCategory()
                                .toString()
                                .equals(category))
                .toList();
    }

    private void setMessageAndMemberMap(Member member, List<Room> rooms,
                                        Map<Long, String> messageMap,
                                        Map<Long, Member> memberMap) {

        rooms.forEach(room -> {
            Long roomId = room.getId();
            List<Chat> chats = chatRepository.findByRoomId(roomId);
            if (!chats.isEmpty()) {
                messageMap.put(
                        roomId,
                        chats.get(chats.size() - 1)
                                .getContent()
                );
            }
            List<Participant> findParticipants = participantRepository.findByRoomId(roomId);
            Member findMember = getOtherMember(member, findParticipants);
            memberMap.put(roomId, findMember);
        });
    }

    public FindChatRoomResponse findChat(Long roomId, Member member) {
        List<Chat> chats = chatRepository.findByRoomId(roomId);

        List<Participant> participants = participantRepository.findByRoomId(roomId);
        Member other = getOtherMember(member, participants);

        List<Chat> myChats = getMyChats(member, chats);
        List<Chat> otherChats = getOtherChats(member, chats);
        return FindChatRoomResponse.of(
                roomId, member, Objects.requireNonNull(other), myChats, otherChats);
    }

    private Member getOtherMember(Member member, List<Participant> participants) {
        return participants.stream()
                .filter(participant ->
                        !participant.getMember()
                                .getEmail()
                                .equals(member.getEmail())
                )
                .findFirst()
                .map(Participant::getMember)
                .orElse(null);
    }

    private List<Chat> getMyChats(Member member, List<Chat> chats) {
        return chats.stream()
                .filter(chat ->
                        chat.getMember()
                                .getEmail()
                                .equals(member.getEmail()))
                .toList();
    }

    private List<Chat> getOtherChats(Member member, List<Chat> chats) {
        return chats.stream()
                .filter(chat ->
                        !chat.getMember()
                                .getEmail()
                                .equals(member.getEmail())
                )
                .toList();
    }

    @Transactional
    public void saveChat(Long roomId, SendChatDTO sendChat, Long memberId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(EntityNotFoundException::new);
        Member member = memberRepository.findById(memberId)
                .orElseThrow(EntityNotFoundException::new);

        Chat chat = buildChat(sendChat, member, room);
        chatRepository.save(chat);
    }

    private Chat buildChat(SendChatDTO sendChat, Member member, Room room) {
        return Chat.builder()
                .content(sendChat.message())
                .member(member)
                .room(room)
                .build();
    }

    @Transactional
    public void participateChat(Long otherMemberId, Member member, String category) {
        List<Participant> otherParticipants = participantRepository.findByMemberId(otherMemberId);
        List<Participant> myParticipants = participantRepository.findByMemberId(member.getId());
        checkExistsChatRoom(otherParticipants, myParticipants);

        Room room = buildRoom(category);
        roomRepository.save(room);

        Participant me = buildParticipant(member, room);

        Member otherMember = memberRepository.findById(otherMemberId)
                .orElseThrow(EntityNotFoundException::new);
        Participant other = buildParticipant(otherMember, room);

        List<Participant> participants = List.of(me, other);
        participantRepository.saveAll(participants);
    }

    private void checkExistsChatRoom(List<Participant> otherParticipants, List<Participant> myParticipants) {
        otherParticipants.forEach(
                otherParticipant ->
                        myParticipants.forEach(
                                myParticipant -> {
                                    Room otherRoom = otherParticipant.getRoom();
                                    Room myRoom = myParticipant.getRoom();
                                    if (otherRoom.getId().equals(myRoom.getId())) {
                                        throw new IllegalArgumentException("Chat Room is already exists");
                                    }
                                }
                        )
        );
    }

    private Room buildRoom(String category) {
        return Room.builder()
                .category(getCategory(category))
                .build();
    }

    public Category getCategory(String category) {
        if (category.equals("SKIN")) {
            return Category.SKIN;
        }
        if (category.equals("HEALTH")) {
            return Category.HEALTH;
        }
        return Category.NUTRIENTS;
    }

    private Participant buildParticipant(Member member, Room room) {
        return Participant.builder()
                .member(member)
                .room(room)
                .build();
    }
}
