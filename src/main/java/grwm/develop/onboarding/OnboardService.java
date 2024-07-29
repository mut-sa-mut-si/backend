package grwm.develop.onboarding;

import grwm.develop.member.Member;
import grwm.develop.onboarding.dto.LandingMemberResponse;
import org.springframework.stereotype.Service;

@Service
public class OnboardService {

    public LandingMemberResponse landing(Member member) {
        return LandingMemberResponse.from(member);
    }
}
