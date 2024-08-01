package grwm.develop.mypage;

import grwm.develop.member.Member;
import grwm.develop.mypage.dto.FindAllSubscribesResponse;
import grwm.develop.subscribe.Subscribe;
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

    public FindAllSubscribesResponse findAllSubscribes(Member member) {
        List<Subscribe> subscribes = subscribeRepository.findAllByMemberId(member.getId());
        return FindAllSubscribesResponse.from(subscribes);
    }

    
}
