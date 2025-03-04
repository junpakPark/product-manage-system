package com.github.junpakpark.productmanage.member.domain;

import com.github.junpakpark.productmanage.common.domain.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, updatable = false, length = 20)
    private String name;
    @Column(nullable = false, unique = true, updatable = false, length = 100)
    private String email;
    @Embedded
    private Password password;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    public Member(
            final String name,
            final String email,
            final Password password,
            final Role role
    ) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public void changePassword(final Password newPassword) {
        this.password = newPassword;
    }

}
