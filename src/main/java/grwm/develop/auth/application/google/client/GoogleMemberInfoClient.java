package grwm.develop.auth.application.google.client;

import grwm.develop.auth.application.google.dto.GoogleMemberProfile;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "googleMemberInfoClient", url = "${google.userInfoUri}")
public interface GoogleMemberInfoClient {

    @GetMapping
    GoogleMemberProfile getMemberProfile(@RequestHeader("Content-Type") String contentType,
                                               @RequestHeader("Authorization") String authorization);
}
