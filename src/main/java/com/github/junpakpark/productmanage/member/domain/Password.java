package com.github.junpakpark.productmanage.member.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Password {

    @Column(nullable = false, length = 100)
    private String password;
    private LocalDateTime lastPasswordChangedAt;

    public Password(String password) {
        this.password = password;
        this.lastPasswordChangedAt = LocalDateTime.now();
    }

}
