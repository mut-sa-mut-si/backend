package grwm.develop.pay.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public record PaymentRequest(String cid,
                             String partnerOrderId,
                             String partnerUserId,
                             String itemName,
                             int quantity,
                             int totalAmount,
                             int taxFreeAmount,
                             String approvalUrl,
                             String cancelUrl,
                             String failUrl) {
}
