package grwm.develop.subscribe;

import grwm.develop.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscribeItemRepository extends JpaRepository<SubscribeItem, Long> {
    public SubscribeItem findByMemberId(Long MemberId);
}
