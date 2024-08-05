package grwm.develop.subscribe;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscribeRepository extends JpaRepository<Subscribe, Long> {

    List<Subscribe> findAllByMemberId(Long memberId);

    List<Subscribe> findBySubscribeItemId(Long subscribeItemId);

    Subscribe findBySubscribeItemIdAndMemberId(Long subscribeItem, Long memberId);

    boolean existsBySubscribeItemIdAndMemberId(Long subscribeItemId, Long memberId);
}
