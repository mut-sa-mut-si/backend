package grwm.develop.member;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional
    public void addPoints(Long memberId, int pointsToAdd) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        member.setPoint(member.getPoint() + pointsToAdd);
        memberRepository.save(member);
    }

    public boolean checkMemberOnboarding(Member member) {
        return member.isOnboarded();
    }
}
