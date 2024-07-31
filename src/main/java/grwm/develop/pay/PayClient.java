package grwm.develop.pay;

import feign.Headers;
import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "PayClient", url = "https://open-api.kakaopay.com")
public interface PayClient {

    @PostMapping( value =" /online/v1/payment/ready", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @Headers("Authorization: {authorization}")
    Map<String,Object> beforePay(@RequestHeader("Authorization") String authorization,
                                 @RequestBody Map<String, String> params);
}
