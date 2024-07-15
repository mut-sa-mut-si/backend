package grwm.develop.auth;

public interface ControllerService {

    String getRedirectURL();

    String authorize(String code);
}
