package grwm.develop.auth.login.presentation;

import grwm.develop.auth.login.presentation.controllerservice.ControllerService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ControllerServiceCondition {

    private final Map<String, ControllerService> controllerServiceMap;

    public ControllerService getControllerService(String name) {
        return controllerServiceMap.get(name);
    }
}
