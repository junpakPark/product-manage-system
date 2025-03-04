package com.github.junpakpark.productmanage.member.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.time.LocalDateTime;
import java.util.Objects;
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

    @Override
    public boolean equals(final Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Password password1 = (Password) o;
        return Objects.equals(password, password1.password) && Objects.equals(lastPasswordChangedAt,
                password1.lastPasswordChangedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(password, lastPasswordChangedAt);
    }
}
