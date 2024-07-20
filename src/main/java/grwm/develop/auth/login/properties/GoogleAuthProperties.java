package grwm.develop.auth.login.properties;

import static grwm.develop.utils.Constants.CLIENT_ID;
import static grwm.develop.utils.Constants.GOOGLE;
import static grwm.develop.utils.Constants.REDIRECT_URI;
import static grwm.develop.utils.Constants.SCOPE;

import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = GOOGLE)
public class GoogleAuthProperties implements AuthProperties {

    private String clientId;
    private String redirectUri;
    private String clientSecret;
    private String requestUri;
    private String scope;
    private String tokenUri;
    private String userInfoUri;

    @Override
    public String mapping() {
        Map<String, String> propertiesMap = getPropertiesMap();
        StringBuilder result = new StringBuilder("redirect:" + requestUri);
        propertiesMap.keySet()
                .forEach(key -> result.append("&").append(key).append("=").append(propertiesMap.get(key)));
        return result.toString();
    }

    private Map<String, String> getPropertiesMap() {
        return Map.of(
                CLIENT_ID, clientId,
                REDIRECT_URI, redirectUri,
                SCOPE, scope);
    }
}
