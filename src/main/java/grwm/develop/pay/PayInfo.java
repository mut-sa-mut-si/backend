package grwm.develop.pay;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PayInfo {
    private String cid;
    private String tid;
    private String orderId;
    private String userId;
}
