package grwm.develop.notification.change;

import grwm.develop.BaseEntity;
import grwm.develop.member.Member;
import grwm.develop.notification.Type;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ChangeReceiver extends BaseEntity {

    @JoinColumn(name = "change_id")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Change change;

    @JoinColumn(name = "member_id")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Member member;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Type type;
}
