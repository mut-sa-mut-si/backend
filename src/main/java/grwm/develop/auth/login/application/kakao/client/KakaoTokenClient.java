package grwm.develop.auth.login.application.kakao.client;

import static grwm.develop.utils.Constants.CLIENT_ID;
import static grwm.develop.utils.Constants.CODE;
import static grwm.develop.utils.Constants.GRANT_TYPE;
import static grwm.develop.utils.Constants.REDIRECT_URI;

import grwm.develop.auth.login.application.kakao.dto.KakaoAuthToken;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "kakaoTokenClient", url = "${kakao.tokenUri}")
public interface KakaoTokenClient {

    @PostMapping
    KakaoAuthToken getAuthToken(@RequestParam(GRANT_TYPE) String grantType,
                                @RequestParam(CLIENT_ID) String clientId,
                                @RequestParam(REDIRECT_URI) String redirectUri,
                                @RequestParam(CODE) String code);
}
