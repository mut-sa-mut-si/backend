package grwm.develop.chat.participant;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {

    List<Participant> findByMemberId(Long memberId);
}
