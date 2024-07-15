package grwm.develop.auth.presentation.controllerservice;

public interface ControllerService {

    String getRedirectURL();

    String authorize(String code);
}
