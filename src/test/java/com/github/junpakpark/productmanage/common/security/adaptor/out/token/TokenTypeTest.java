package com.github.junpakpark.productmanage.common.security.adaptor.out.token;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TokenTypeTest {

    @Test
    @DisplayName("TokenType은 ACCESS와 REFRESH 두 가지 타입만 가진다.")
    void enumValuesTest() {
        // Action
        TokenType[] values = TokenType.values();

        // Assert
        assertThat(values).containsExactly(TokenType.ACCESS, TokenType.REFRESH);
    }

    @Test
    @DisplayName("ACCESS 타입일 때 isAccess()는 true를 반환한다.")
    void isAccess_returnsTrueForAccess() {
        // Arrange
        final TokenType tokenType = TokenType.ACCESS;

        // Action
        final boolean isAccess = tokenType.isAccess();

        // Assert
        assertThat(isAccess).isTrue();
    }

    @Test
    @DisplayName("REFRESH 타입일 때 isAccess()는 false를 반환한다.")
    void isAccess_returnsFalseForRefresh() {
        // Arrange
        final TokenType tokenType = TokenType.REFRESH;

        // Action
        final boolean isAccess = tokenType.isAccess();

        // Assert
        assertThat(isAccess).isFalse();
    }
}
