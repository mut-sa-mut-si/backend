package grwm.develop.member;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public void addPoints(Long memberId, int pointsToAdd) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        member.setPoint(member.getPoint() + pointsToAdd);
        memberRepository.save(member);
    }
}
