package grwm.develop.auth.application.google.client;


import static grwm.develop.utils.Constants.CLIENT_ID;
import static grwm.develop.utils.Constants.CLIENT_SECRET;
import static grwm.develop.utils.Constants.CODE;
import static grwm.develop.utils.Constants.GRANT_TYPE;
import static grwm.develop.utils.Constants.REDIRECT_URI;

import grwm.develop.auth.application.google.dto.GoogleAuthToken;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "googleTokenClient", url = "${google.tokenUri}")
public interface GoogleTokenClient {

    @PostMapping
    GoogleAuthToken getAuthToken(@RequestParam(GRANT_TYPE) String grantType,
                                 @RequestParam(CODE) String code,
                                 @RequestParam(CLIENT_ID) String clientId,
                                 @RequestParam(REDIRECT_URI) String requestUri,
                                 @RequestParam(CLIENT_SECRET) String clientSecret);
}
