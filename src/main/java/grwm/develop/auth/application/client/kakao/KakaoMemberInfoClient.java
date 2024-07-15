package grwm.develop.auth.application.client.kakao;

import grwm.develop.auth.application.dto.KakaoMemberProfile;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "kakaoMemberInfoClient", url = "https://kapi.kakao.com")
public interface KakaoMemberInfoClient {

    @GetMapping(value = "/v2/user/me")
    KakaoMemberProfile getUserProfile(@RequestHeader("Authorization") String authorization);
}
