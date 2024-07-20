package grwm.develop.auth.login.presentation.controllerservice;

import static grwm.develop.utils.Constants.KAKAO;

import grwm.develop.auth.login.application.kakao.KakaoAuthService;
import grwm.develop.auth.login.properties.KakaoAuthProperties;
import grwm.develop.member.Member;
import grwm.develop.utils.URLUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component(KAKAO)
@RequiredArgsConstructor
public class KakaoControllerService implements ControllerService {

    private final KakaoAuthProperties authProperties;
    private final KakaoAuthService authService;

    @Override
    public String getRedirectURL() {
        return URLUtils.createURL(authProperties);
    }

    @Override
    public Member authorize(String code) {
        return authService.authorization(code);
    }
}
