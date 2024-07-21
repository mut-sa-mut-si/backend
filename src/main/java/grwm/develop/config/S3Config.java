package grwm.develop.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import grwm.develop.utils.S3Properties;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class S3Config {

    private static final String ACCESS_KEY = "accessKey";
    private static final String SECRET_KEY = "secretKey";
    private static final String STATIC = "static";

    private final S3Properties s3Properties;

    @Bean
    public AmazonS3Client amazonS3Client() {
        Map<String, String> credentials = s3Properties.getCredentials();
        BasicAWSCredentials awsCredentials =
                new BasicAWSCredentials(credentials.get(ACCESS_KEY), credentials.get(SECRET_KEY));
        return (AmazonS3Client) AmazonS3ClientBuilder.standard()
                .withRegion(s3Properties.getRegion().get(STATIC))
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .build();
    }
}
