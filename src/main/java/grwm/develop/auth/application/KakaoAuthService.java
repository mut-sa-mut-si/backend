package grwm.develop.auth.application;

import static grwm.develop.auth.Constants.AUTHORIZATION_CODE;
import static grwm.develop.auth.Constants.TOKEN_PREFIX;

import grwm.develop.auth.application.client.kakao.KakaoMemberInfoClient;
import grwm.develop.auth.application.client.kakao.KakaoTokenClient;
import grwm.develop.auth.application.dto.KakaoMemberProfile;
import grwm.develop.auth.properties.KakaoAuthProperties;
import grwm.develop.member.Member;
import grwm.develop.member.MemberRepository;
import jakarta.persistence.EntityExistsException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class KakaoAuthService implements AuthService {

    private final MemberRepository memberRepository;

    private final KakaoAuthProperties authProperties;

    private final KakaoTokenClient kakaoTokenClient;
    private final KakaoMemberInfoClient kakaoMemberInfoClient;

    @Override
    @Transactional
    public String authorization(String code) {
        String token = getAccessToken(code);
        KakaoMemberProfile profile = kakaoMemberInfoClient.getUserProfile(TOKEN_PREFIX + token);
        Member member = profile.toEntity();
        checkedDuplicateEmail(member);
        memberRepository.save(member);
        return member.toString();
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
