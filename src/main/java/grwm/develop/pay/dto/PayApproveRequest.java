package grwm.develop.pay.dto;


import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public record PayApproveRequest(String cid,
                                String tid,
                                String partnerOrderId,
                                String partnerUserId,
                                String pgToken) {
}
