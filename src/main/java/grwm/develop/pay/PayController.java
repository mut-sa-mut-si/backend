package grwm.develop.pay;

import grwm.develop.auth.security.UserDetailsImpl;
import grwm.develop.pay.dto.PayInfoRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1/payment")
public class PayController {

    private final PayService payService;
    private MemberInfo memberInfo;

    @GetMapping
    public String beforePay(@RequestBody PayInfoRequest request,
                            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return "redirect:" + payService.beforePay(request, userDetails.member());
    }

    @ResponseBody
    @GetMapping("/redirect")
    public ResponseEntity<String> afterPay(@RequestParam("pg_token") String pgToken) {
        payService.afterPay(pgToken, memberInfo.getMemberId(), memberInfo.getSubscribeId());
        return ResponseEntity.ok().body("success");
    }
}
