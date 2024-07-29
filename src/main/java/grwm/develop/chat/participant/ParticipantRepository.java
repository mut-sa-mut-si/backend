package grwm.develop.chat.participant;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {

    @Query("select p from Participant p join fetch p.room join fetch p.member")
    List<Participant> findByMemberId(Long memberId);

    @Query("select p from Participant p join fetch p.room join fetch p.member")
    List<Participant> findByRoomId(Long roomId);
}
