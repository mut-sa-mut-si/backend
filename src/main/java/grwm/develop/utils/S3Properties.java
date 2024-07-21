package grwm.develop.utils;

import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "cloud.aws")
public class S3Properties {

    private Map<String, String> s3;
    private Map<String, String> credentials;
    private Map<String, String> region;
}
