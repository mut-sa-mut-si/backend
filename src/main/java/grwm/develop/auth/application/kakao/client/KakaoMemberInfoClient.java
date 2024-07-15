package grwm.develop.auth.application.kakao.client;

import static grwm.develop.utils.Constants.AUTHORIZATION;

import grwm.develop.auth.application.kakao.dto.KakaoMemberProfile;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "kakaoMemberInfoClient", url = "${kakao.userInfoUri}")
public interface KakaoMemberInfoClient {

    @GetMapping
    KakaoMemberProfile getMemberProfile(@RequestHeader(AUTHORIZATION) String authorization);
}
