package grwm.develop.auth.controllerservice;

public interface ControllerService {

    String getRedirectURL();

    String authorize(String code);
}
