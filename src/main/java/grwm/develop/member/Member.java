package grwm.develop.member;

import grwm.develop.BaseEntity;
import grwm.develop.LoginType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.*;


@Entity
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {

    @Column(length = 16, nullable = false)
    private String name;

    @Column(length = 128, nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private LoginType service;

}
