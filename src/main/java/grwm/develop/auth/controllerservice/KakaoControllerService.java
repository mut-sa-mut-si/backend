package grwm.develop.auth.controllerservice;

import static grwm.develop.auth.Constants.KAKAO;

import grwm.develop.auth.application.KakaoAuthService;
import grwm.develop.auth.properties.KakaoAuthProperties;
import grwm.develop.auth.utils.URLUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component(KAKAO)
@RequiredArgsConstructor
public class KakaoControllerService implements ControllerService {

    private final KakaoAuthProperties authProperties;
    private final KakaoAuthService openFeignAuthService;

    @Override
    public String getRedirectURL() {
        return URLUtils.createURL(authProperties);
    }

    @Override
    public String authorize(String code) {
        return openFeignAuthService.authorization(code);
    }
}
