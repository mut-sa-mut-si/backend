package grwm.develop.auth.presentation.controllerservice;

import static grwm.develop.utils.Constants.GOOGLE;

import grwm.develop.auth.application.google.GoogleAuthService;
import grwm.develop.auth.properties.GoogleAuthProperties;
import grwm.develop.utils.URLUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component(GOOGLE)
@RequiredArgsConstructor
public class GoogleControllerService implements ControllerService {

    private final GoogleAuthProperties googleAuthProperties;
    private final GoogleAuthService authService;

    @Override
    public String getRedirectURL() {
        return URLUtils.createURL(googleAuthProperties);
    }

    @Override
    public String authorize(String code) {
        return authService.authorization(code);
    }
}
