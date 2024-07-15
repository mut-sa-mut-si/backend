package grwm.develop.auth.controllerservice;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("google")
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
