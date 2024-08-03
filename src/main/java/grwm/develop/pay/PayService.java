package grwm.develop.pay;

import grwm.develop.member.Member;
import grwm.develop.member.MemberRepository;
import grwm.develop.pay.dto.PayApproveRequest;
import grwm.develop.pay.dto.PayInfoRequest;
import grwm.develop.pay.dto.PaymentRequest;
import grwm.develop.pay.dto.PaymentResponse;
import grwm.develop.subscribe.Subscribe;
import grwm.develop.subscribe.SubscribeItem;
import grwm.develop.subscribe.SubscribeItemRepository;
import grwm.develop.subscribe.SubscribeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PayService {

    private final PayClient payClient;
    private PayInfo payInfo;
    private MemberInfo memberInfo;
    private final MemberRepository memberRepository;
    private final SubscribeRepository subscribeRepository;
    private final SubscribeItemRepository subscribeItemRepository;

    public String beforePay(PayInfoRequest request, Member member) {
        int quantity = 1;
        String orderId = "123456789";
        String cid = "TC0ONETIME";
        PaymentRequest paymentRequest = getPaymentRequest(request, member, quantity, cid, orderId);
        String secretKey = "SECRET_KEY DEVE8ED784F13A714C623A9D1C3DC2541EB1C784";

        PaymentResponse response = payClient.beforePay(secretKey, paymentRequest);
        creatPayInfo(member, cid, orderId, response);
        createMemberInfo(request, member);

        return response.nextRedirectPcUrl();
    }

    private void createMemberInfo(PayInfoRequest request, Member member) {
        memberInfo = new MemberInfo();
        memberInfo.setMemberId(request.memberId());
        memberInfo.setSubscribeId(member.getId());
    }

    private void creatPayInfo(Member member, String cid, String orderId, PaymentResponse response) {
        payInfo = new PayInfo();
        payInfo.setCid(cid);
        payInfo.setOrderId(orderId);
        payInfo.setUserId(String.valueOf(member.getId()));
        payInfo.setTid(response.tid());
    }

    private static PaymentRequest getPaymentRequest(PayInfoRequest request,
                                                    Member member,
                                                    int quantity,
                                                    String cid,
                                                    String orderId) {
        return new PaymentRequest(
                cid,
                orderId,
                String.valueOf(member.getId()),
                request.itemName(),
                quantity,
                request.totalAmount(),
                request.totalAmount(),
                "http://localhost:8080/api/v1/payment/redirect",
                "http://localhost:8080/api/v1/payment/cancel",
                "http://localhost:8080/api/v1/payment/fail"
        );
    }

    public void afterPay(String pgToken) {
        PayApproveRequest payApproveRequest = getApproveRequest(pgToken);
        String secretKey = "SECRET_KEY DEVE8ED784F13A714C623A9D1C3DC2541EB1C784";
        payClient.afterPay(secretKey, payApproveRequest);
        createSubscribe();
    }

    private void createSubscribe() {
        Member member = memberRepository.findById(memberInfo.getSubscribeId())
                .orElseThrow(() -> new IllegalArgumentException("멤버를 찾을 수 없습니다."));
        SubscribeItem subscribeItem = subscribeItemRepository.findByMemberId(memberInfo.getMemberId());
        Subscribe subscribe = Subscribe.builder()
                .member(member)
                .subscribeItem(subscribeItem)
                .build();
        subscribeRepository.save(subscribe);
    }

    private PayApproveRequest getApproveRequest(String pgToken) {
        return new PayApproveRequest(
                payInfo.getCid(),
                payInfo.getTid(),
                payInfo.getOrderId(),
                payInfo.getUserId(),
                pgToken
        );
    }
}
