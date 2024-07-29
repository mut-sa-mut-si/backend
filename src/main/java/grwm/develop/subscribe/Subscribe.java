package grwm.develop.subscribe;

import grwm.develop.BaseEntity;
import grwm.develop.member.Member;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

@Entity
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Subscribe extends BaseEntity {

    @JoinColumn(name = "member_id")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Member member;

    @JoinColumn(name = "subscribe_item_id")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private SubscribeItem subscribeItem;
}
