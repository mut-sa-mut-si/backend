package grwm.develop.pay.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.Date;

@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public record PaymentResponse(String tid,
                              String nextRedirectPcUrl,
                              Date createdAt) {
}
