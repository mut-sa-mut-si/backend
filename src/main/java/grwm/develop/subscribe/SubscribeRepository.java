package grwm.develop.subscribe;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubscribeRepository extends JpaRepository<Subscribe,Long> {
    List<Subscribe> findAllByMemberId(Long MemberId);
}
