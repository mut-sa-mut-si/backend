package grwm.develop.auth.application.google;

import static grwm.develop.utils.Constants.AUTHORIZATION_CODE;
import static grwm.develop.utils.Constants.TOKEN_PREFIX;
import static grwm.develop.utils.Constants.X_WWW_URL_ENCODED_TYPE;

import grwm.develop.auth.application.AuthService;
import grwm.develop.auth.application.google.client.GoogleMemberInfoClient;
import grwm.develop.auth.application.google.client.GoogleTokenClient;
import grwm.develop.auth.application.google.dto.GoogleMemberProfile;
import grwm.develop.auth.properties.GoogleAuthProperties;
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
public class GoogleAuthService implements AuthService {

    private final MemberRepository memberRepository;

    private final GoogleAuthProperties googleAuthProperties;

    private final GoogleTokenClient googleTokenClient;
    private final GoogleMemberInfoClient googleUserInfoClient;

    @Override
    @Transactional
    public String authorization(String code) {
        String token = getAccessToken(code);
        GoogleMemberProfile profile = getMemberProfile(token);
        Member member = profile.toEntity();
        checkedDuplicateEmail(member);
        memberRepository.save(member);
        return member.toString();
    }

    private String getAccessToken(String code) {
        return googleTokenClient.getAuthToken(
                        AUTHORIZATION_CODE,
                        code,
                        googleAuthProperties.getClientId(),
                        googleAuthProperties.getRedirectUri(),
                        googleAuthProperties.getClientSecret())
                .accessToken();
    }

    private GoogleMemberProfile getMemberProfile(String token) {
        return googleUserInfoClient.getMemberProfile(
                X_WWW_URL_ENCODED_TYPE,
                TOKEN_PREFIX + token);
    }

    private void checkedDuplicateEmail(Member other) {
        Optional<Member> optionalUser = memberRepository.findByEmail(other.getEmail());
        if (optionalUser.isPresent()) {
            throw new EntityExistsException("duplicate email");
        }
    }
}

