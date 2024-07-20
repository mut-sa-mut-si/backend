package grwm.develop.auth.login.presentation.controllerservice;

import grwm.develop.member.Member;

public interface ControllerService {

    String getRedirectURL();

    Member authorize(String code);
}
