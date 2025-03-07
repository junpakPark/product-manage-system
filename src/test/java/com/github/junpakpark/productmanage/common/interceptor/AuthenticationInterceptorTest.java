package com.github.junpakpark.productmanage.common.interceptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.github.junpakpark.productmanage.common.resolver.memberinfo.MemberInfo;
import com.github.junpakpark.productmanage.common.security.application.port.out.token.TokenValidator;
import com.github.junpakpark.productmanage.common.security.exception.TokenErrorCode;
import com.github.junpakpark.productmanage.common.security.exception.TokenUnauthorizedException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AuthenticationInterceptorTest {

    private AuthenticationInterceptor sut;

    private HttpServletRequest request;
    private HttpServletResponse response;
    private final Object handler = new Object();

    @BeforeEach
    void setUp() {
        sut = new AuthenticationInterceptor(getFakeTokenValidator());
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
    }


    @Test
    @DisplayName("정상적인 AccessToken 요청은 통과된다")
    void validAccessToken_passes() {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Bearer valid-access-token");

        // Action
        final boolean result = sut.preHandle(request, response, handler);

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Authorization 헤더가 없으면 예외 발생")
    void missingAuthorizationHeader_throwsException() {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn(null);

        // Action & Assert
        assertThatThrownBy(() -> sut.preHandle(request, response, handler))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("토큰이 유효하지 않으면 예외 발생")
    void invalidToken_throwsAuthenticationException() {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Bearer invalid-token");

        // Action & Assert
        assertThatThrownBy(() -> sut.preHandle(request, response, handler))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("AccessToken이 아니면 TokenUnauthorizedException 발생")
    void notAccessToken_throwsAuthenticationException() {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Bearer not-access-token");

        // Action & Assert
        assertThatThrownBy(() -> sut.preHandle(request, response, handler))
                .isInstanceOf(TokenUnauthorizedException.class)
                .hasMessage("Access Token이 아닙니다.");
    }

    private TokenValidator getFakeTokenValidator() {
        return new TokenValidator() {

            @Override
            public MemberInfo parseToken(final String token) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void validateToken(final String token) {
                if ("invalid-token".equals(token)) {
                    throw new IllegalArgumentException();
                }
            }

            @Override
            public void validateAccessToken(final String token) {
                if ("not-access-token".equals(token)) {
                    throw new TokenUnauthorizedException(TokenErrorCode.NOT_ACCESS_TOKEN);
                }
            }
        };
    }

}
