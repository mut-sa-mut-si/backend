package grwm.develop.auth.controllerservice;

import static grwm.develop.auth.Constants.GOOGLE;

import grwm.develop.auth.properties.GoogleAuthProperties;
import grwm.develop.auth.utils.URLUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component(GOOGLE)
@RequiredArgsConstructor
public class GoogleControllerService implements ControllerService {

    private final GoogleAuthProperties googleAuthProperties;
    private final GoogleOpenFeignAuthService googleOpenFeignAuthService;

    @Override
    public String getRedirectURL() {
        return URLUtils.createURL(googleAuthProperties);
    }

    @Override
    public String authorize(String code) {
        return googleOpenFeignAuthService.authorization(code);
    }
}
