package com.github.junpakpark.productmanage.common.security.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class AuthorizationHeaderExtractorTest {

    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        request = mock(HttpServletRequest.class);
    }

    @Nested
    class ValidRequest {

        @Test
        @DisplayName("정상적인 Authorization 헤더에서 토큰을 추출한다")
        void extractToken_withValidAuthorizationHeader() {
            // Arrange
            when(request.getHeader("Authorization")).thenReturn("Bearer valid-token-value");

            // Action
            final String token = AuthorizationHeaderExtractor.extractToken(request);

            // Assert
            assertThat(token).isEqualTo("valid-token-value");
        }

        @Test
        @DisplayName("Bearer 대소문자 구분 없이 정상적으로 토큰을 추출한다")
        void extractToken_withCaseInsensitiveBearer() {
            // Arrange
            when(request.getHeader("Authorization")).thenReturn("bEaReR case-insensitive-token");

            // Action
            final String token = AuthorizationHeaderExtractor.extractToken(request);

            // Assert
            assertThat(token).isEqualTo("case-insensitive-token");
        }

    }

    @Nested
    class InvalidRequest {

        @Test
        @DisplayName("Authorization 헤더가 없는 경우 예외를 던진다")
        void extractToken_withMissingAuthorizationHeader() {
            // Arrange
            when(request.getHeader("Authorization")).thenReturn(null);

            // Action & Assert
            assertThatThrownBy(() -> AuthorizationHeaderExtractor.extractToken(request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Authorization header is missing");
        }

        @Test
        @DisplayName("Authorization 헤더가 비어있는 경우 예외를 던진다")
        void extractToken_withEmptyAuthorizationHeader() {
            // Arrange
            when(request.getHeader("Authorization")).thenReturn("");

            // Action & Assert
            assertThatThrownBy(() -> AuthorizationHeaderExtractor.extractToken(request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Authorization header is missing");
        }

        @Test
        @DisplayName("Bearer 형식이 아닌 Authorization 헤더는 예외를 던진다")
        void extractToken_withInvalidAuthorizationHeaderFormat() {
            // Arrange
            when(request.getHeader("Authorization")).thenReturn("Basic some-token-value");

            // Action & Assert
            assertThatThrownBy(() -> AuthorizationHeaderExtractor.extractToken(request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Authorization header is invalid");
        }

        @Test
        @DisplayName("Authorization 헤더가 Bearer지만 토큰이 없는 경우 예외를 던진다")
        void extractToken_withIncompleteBearerAuthorizationHeader() {
            // Arrange
            when(request.getHeader("Authorization")).thenReturn("Bearer ");

            // Action & Assert
            assertThatThrownBy(() -> AuthorizationHeaderExtractor.extractToken(request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Authorization header is invalid");
        }

    }

}
