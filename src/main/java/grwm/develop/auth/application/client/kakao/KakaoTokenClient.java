package grwm.develop.auth.application.client.kakao;

import grwm.develop.auth.application.KakaoAuthToken;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "kakaoTokenClient", url = "https://kauth.kakao.com")
public interface KakaoTokenClient {

    @PostMapping(value = "/oauth/token")
    KakaoAuthToken getAuthToken(@RequestParam("grant_type") String grantType,
                                @RequestParam("client_id") String clientId,
                                @RequestParam("redirect_uri") String redirectUri,
                                @RequestParam("code") String code);
}
