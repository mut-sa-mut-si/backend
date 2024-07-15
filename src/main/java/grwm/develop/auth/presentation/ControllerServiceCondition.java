package grwm.develop.auth.presentation;

import grwm.develop.auth.presentation.controllerservice.ControllerService;
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
