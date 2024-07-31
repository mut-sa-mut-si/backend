package grwm.develop.pay;

import grwm.develop.auth.security.UserDetailsImpl;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payment")
public class PayController {

    private final PayService payService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> beforePay(@RequestParam("type") String type,
                                                         @RequestBody PayInfoRequest request,
                                                         @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Map<String, Object> response = payService.beforePay(type, request, userDetails.member());
        return ResponseEntity.ok(response);
    }
}
