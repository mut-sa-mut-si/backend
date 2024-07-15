package grwm.develop.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("kakao")
@RequiredArgsConstructor
public class KakaoControllerService implements ControllerService {

    private final KakaoAuthProperties authProperties;
    private final KakaoOpenFeignAuthService openFeignAuthService;

    @Override
    public String getRedirectURL() {
        return URLUtils.createURL(authProperties);
    }

    @Override
    public String authorize(String code) {
        return openFeignAuthService.authorization(code);
    }
}
