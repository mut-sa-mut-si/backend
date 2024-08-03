package grwm.develop.pay.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.List;

@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public record PayApproveResponse(String aid,
                                 String tid,
                                 String cid,
                                 String sid,
                                 String partnerUserId,
                                 List<Amount> amount,
                                 String itemName,
                                 String itemCode,
                                 int quantity,
                                 String payload) {
    public record Amount(
            Long total,
            Long taxFree,
            Long vat,
            Long point,
            Long discount,
            Long greenDeposit
    ) {
    }

    public record CardInfo(
            String purchaseCorp,
            String purchaseCorpCode,
            String issuerCorp,
            String issuerCorpCode,
            String bin,
            String cardType,
            String installMonth,
            String approvedId,
            String cardMid,
            String freeInstall,
            String InstallmentType,
            String cardItemCode
    ) {
    }
}
