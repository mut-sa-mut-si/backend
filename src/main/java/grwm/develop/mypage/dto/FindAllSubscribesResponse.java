package grwm.develop.mypage.dto;

import grwm.develop.subscribe.Subscribe;
import java.util.List;

public record FindAllSubscribesResponse(Long memberId, List<Subscriber> subscribers) {

    public static FindAllSubscribesResponse from(Long memberId, List<Subscribe> subscribes) {
        return new FindAllSubscribesResponse(
                memberId,
                subscribes.stream()
                        .map(subscribe ->
                                new Subscriber(
                                        subscribe.getSubscribeItem().getMember().getId(),
                                        subscribe.getSubscribeItem().getMember().getName()))
                        .toList()
        );
    }

    public record Subscriber(Long id, String name) {
    }
}
