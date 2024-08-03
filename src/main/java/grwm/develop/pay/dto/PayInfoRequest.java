package grwm.develop.pay.dto;

public record PayInfoRequest(Long memberId, String itemName, int totalAmount) {
}
