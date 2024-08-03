package grwm.develop.pay;

import grwm.develop.pay.dto.PayApproveRequest;
import grwm.develop.pay.dto.PayApproveResponse;
import grwm.develop.pay.dto.PaymentRequest;
import grwm.develop.pay.dto.PaymentResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "PayClient", url = "https://open-api.kakaopay.com")
public interface PayClient {

    @PostMapping(value = "/online/v1/payment/ready", consumes = MediaType.APPLICATION_JSON_VALUE)
    PaymentResponse beforePay(@RequestHeader("Authorization") String secretKey,
                              @RequestBody PaymentRequest params);

    @PostMapping(value = "/online/v1/payment/approve", consumes = MediaType.APPLICATION_JSON_VALUE)
    PayApproveResponse afterPay(@RequestHeader("Authorization") String secretKey,
                                @RequestBody PayApproveRequest params);
}
