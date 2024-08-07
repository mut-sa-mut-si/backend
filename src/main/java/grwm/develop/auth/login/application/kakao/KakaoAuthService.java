package grwm.develop.auth.login.application.kakao;

import static grwm.develop.utils.Constants.AUTHORIZATION_CODE;
import static grwm.develop.utils.Constants.TOKEN_PREFIX;

import grwm.develop.auth.login.application.AuthService;
import grwm.develop.auth.login.application.kakao.client.KakaoMemberInfoClient;
import grwm.develop.auth.login.application.kakao.client.KakaoTokenClient;
import grwm.develop.auth.login.application.kakao.dto.KakaoMemberProfile;
import grwm.develop.auth.login.properties.KakaoAuthProperties;
import grwm.develop.member.Member;
import grwm.develop.member.MemberRepository;
import grwm.develop.subscribe.SubscribeItem;
import grwm.develop.subscribe.SubscribeItemRepository;
import jakarta.persistence.EntityExistsException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class KakaoAuthService implements AuthService {

    private final MemberRepository memberRepository;
    private final SubscribeItemRepository subscribeItemRepository;

    private final KakaoAuthProperties authProperties;

    private final KakaoTokenClient kakaoTokenClient;
    private final KakaoMemberInfoClient kakaoMemberInfoClient;

    @Override
    @Transactional
    public Member authorization(String code) {
        String token = getAccessToken(code);
        KakaoMemberProfile profile = kakaoMemberInfoClient.getMemberProfile(TOKEN_PREFIX + token);
        Member member = profile.toEntity();
        try {
            checkedDuplicateEmail(member);
            memberRepository.save(member);
            if (!subscribeItemRepository.existsByMemberId(member.getId())) {
                subscribeItemRepository.save(
                        SubscribeItem.builder()
                                .member(member)
                                .price(1900)
                                .build());
            }
            return member;
        } catch (EntityExistsException e) {
            return member;
        }
    }

    private String getAccessToken(String code) {
        return kakaoTokenClient.getAuthToken(
                        AUTHORIZATION_CODE,
                        authProperties.getClientId(),
                        authProperties.getRedirectUri(),
                        code)
                .accessToken();
    }

    private void checkedDuplicateEmail(Member other) {
        Optional<Member> optionalUser = memberRepository.findByEmail(other.getEmail());
        if (optionalUser.isPresent()) {
            throw new EntityExistsException("duplicate email");
        }
    }
}
