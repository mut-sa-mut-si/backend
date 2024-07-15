package grwm.develop.auth.presentation.controllerservice;

import static grwm.develop.utils.Constants.KAKAO;

import grwm.develop.auth.application.kakao.KakaoAuthService;
import grwm.develop.auth.properties.KakaoAuthProperties;
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
    public String authorize(String code) {
        return authService.authorization(code);
    }
}
