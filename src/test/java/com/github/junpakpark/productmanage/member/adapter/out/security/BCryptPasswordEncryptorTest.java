package com.github.junpakpark.productmanage.member.adapter.out.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import com.github.junpakpark.productmanage.member.domain.Password;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

class BCryptPasswordEncryptorTest {

    private BCryptPasswordEncryptor sut;

    @BeforeEach
    void setUp() {
        final PasswordEncoder passwordEncoder = new PasswordEncoder() {
            @Override
            public String encode(final CharSequence rawPassword) {
                return "encoded-password";
            }

            @Override
            public boolean matches(final CharSequence rawPassword, final String encodedPassword) {
                if (encodedPassword.equals("encoded-password")) {
                    return "plain-text".contentEquals(rawPassword);
                }
                return false;
            }
        };

        sut = new BCryptPasswordEncryptor(passwordEncoder);
    }

    @Test
    @DisplayName("비밀번호 암호화가 정상적으로 수행된다.")
    void encodePassword() {
        // Arrange
        final String rawPassword = "plain-text";
        final String encodedPassword = "encoded-password";

        // Action
        final Password result = sut.encode(rawPassword);

        // Assert
        SoftAssertions.assertSoftly(softly -> {
            assertThat(result).isNotNull();
            assertThat(result.getPassword()).isEqualTo(encodedPassword);
        });
    }

    @Test
    @DisplayName("비밀번호가 일치하면 예외 없이 통과한다.")
    void validatePasswordSuccess() {
        // Arrange
        final String rawPassword = "plain-text";
        final Password encodedPassword = new Password("encoded-password");

        // Action & Assert
        assertDoesNotThrow(() -> sut.validatePassword(rawPassword, encodedPassword));

    }

    @Test
    @DisplayName("비밀번호가 불일치하면 예외가 발생한다.")
    void validatePasswordFail() {
        // given
        final String rawPassword = "not-plain-text";
        final Password encodedPassword = new Password("encoded-password");

        // when & then
        assertThatThrownBy(() -> sut.validatePassword(rawPassword, encodedPassword))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid password");
    }
}
