package com.github.junpakpark.productmanage.member.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PasswordTest {

    @Test
    @DisplayName("비밀번호 생성 시, 값과 변경시간이 설정된다.")
    void createPassword() {
        // Arrange
        String rawPassword = "secure-password";

        // Action
        final Password sut = new Password(rawPassword);
        final LocalDateTime now = LocalDateTime.now();

        // then
        SoftAssertions.assertSoftly(softly -> {
            assertThat(sut.getPassword()).isEqualTo(rawPassword);
            assertThat(sut.getLastPasswordChangedAt()).isNotNull();
            assertThat(sut.getLastPasswordChangedAt()).isBeforeOrEqualTo(now);
        });

    }
}
