package grwm.develop.pay;

import grwm.develop.member.Member;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PayService {

    private final PayClient payClient;

    public Map<String, Object> beforePay(String type, PayInfoRequest request, Member member) {
        int quantity = 1;
        int[] amounts = amount(type);
        int totalAmount = amounts[0];
        int taxFreeAmount = amounts[1];
        Map<String, String> params = getPayInfo(request, member, quantity, totalAmount, taxFreeAmount);
        String adminKey = "adminKey 넣기";
        String authorization = "KakaoAK " + adminKey;
        return payClient.beforePay(authorization, params);
    }

    private static Map<String, String> getPayInfo(PayInfoRequest request,
                                                  Member member,
                                                  int quantity,
                                                  int totalAmount,
                                                  int taxFreeAmount) {
        Map<String, String> params = new HashMap<>();
        params.put("cid", "가맹점 코드");
        params.put("partner_order_id", request.orderId());
        params.put("partner_user_id", String.valueOf(member.getId()));
        params.put("item_name", request.itemName());
        params.put("quantity", String.valueOf(quantity));
        params.put("total_amount", String.valueOf(totalAmount));
        params.put("tax_free_amount", String.valueOf(taxFreeAmount));
        params.put("approval_url", "http://localhost:8080/api/v1/payment/success");
        params.put("cancel_url", "http://localhost:8080/api/v1/payment/cancel");
        params.put("fail_url", "http://localhost:8080/api/v1/payment/fail");
        return params;
    }

    private int[] amount(String type) {
        int[] amounts = new int[2];
        if (type.equals("SUBSCRIBE")) {
            amounts[0] = 1900;     //임의로 작성
            amounts[1] = 100;    //임의로 작성
            return amounts;
        }
        if (type.equals("RECIPE")) {
            amounts[0] = 500;     //임의로 작성
            amounts[1] = 10;    //임의로 작성
            return amounts;
        }
        return amounts;
    }
}
