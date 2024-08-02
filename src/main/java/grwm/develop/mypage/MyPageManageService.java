package grwm.develop.mypage;

import grwm.develop.member.Member;
import grwm.develop.mypage.dto.FindAllSubscribesResponse;
import grwm.develop.subscribe.Subscribe;
import grwm.develop.subscribe.SubscribeItem;
import grwm.develop.subscribe.SubscribeItemRepository;
import grwm.develop.subscribe.SubscribeRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MyPageManageService {

    private final SubscribeRepository subscribeRepository;
    private final SubscribeItemRepository subscribeItemRepository;

    public FindAllSubscribesResponse findAllSubscribes(Member member) {
        List<Subscribe> subscribes = subscribeRepository.findAllByMemberId(member.getId());
        return FindAllSubscribesResponse.from(member.getId(), subscribes);
    }

    @Transactional
    public void deleteSubscribe(Long subscribeId, Member member) {
        SubscribeItem subscribeItem = subscribeItemRepository.findByMemberId(subscribeId);
        Subscribe subscribe = subscribeRepository.findBySubscribeItemId(subscribeItem.getId());
        if (!member.getId().equals(subscribe.getMember().getId())) {
            throw new IllegalArgumentException("Not matches member");
        }
        subscribeRepository.delete(subscribe);
    }
}
