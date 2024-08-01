package grwm.develop.onboarding;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OnboardRepository extends JpaRepository<Onboard, Long> {
    List<Onboard> findAllByMemberId(Long memberId);
}
