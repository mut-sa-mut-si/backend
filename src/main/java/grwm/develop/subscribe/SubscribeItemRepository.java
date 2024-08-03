package grwm.develop.subscribe;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscribeItemRepository extends JpaRepository<SubscribeItem, Long> {
    SubscribeItem findByMemberId(Long memberId);

    boolean existsByMemberId(Long memberId);
}
