package grwm.develop.auth.login.application;

import grwm.develop.member.Member;

public interface AuthService {

    Member authorization(String code);
}
