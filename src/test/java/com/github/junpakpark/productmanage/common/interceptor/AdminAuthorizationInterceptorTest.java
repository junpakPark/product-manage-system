package com.github.junpakpark.productmanage.common.interceptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.github.junpakpark.productmanage.common.domain.Role;
import com.github.junpakpark.productmanage.common.resolver.memberinfo.MemberInfo;
import com.github.junpakpark.productmanage.common.security.application.port.out.token.TokenValidator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.nio.file.AccessDeniedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AdminAuthorizationInterceptorTest {

    private AdminAuthorizationInterceptor sut;

    private HttpServletRequest request;
    private HttpServletResponse response;
    private final Object handler = new Object();


    @BeforeEach
    void setUp() {
        sut = new AdminAuthorizationInterceptor(getFakeTokenValidator());

        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
    }

    @Test
    @DisplayName("관리자 권한 토큰일 경우 요청이 통과된다")
    void adminToken_passes() throws Exception {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Bearer admin-token");

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

        // Act & Assert
        assertThatThrownBy(() -> sut.preHandle(request, response, handler))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("알 수 없는 토큰이면 AccessDeniedException 발생")
    void unknownToken_throwsAccessDeniedException() {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Bearer unknown-token");

        // Act & Assert
        assertThatThrownBy(() -> sut.preHandle(request, response, handler))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("관리자 권한이 필요합니다.");
    }

    @Test
    @DisplayName("관리자 권한 토큰이 아닐 경우 AccessDeniedException 발생")
    void nonAdminToken_throwsAccessDeniedException() {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Bearer buyer-token");

        // Act & Assert
        assertThatThrownBy(() -> sut.preHandle(request, response, handler))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("관리자 권한이 필요합니다.");
    }

    private TokenValidator getFakeTokenValidator() {
        return new TokenValidator() {
            @Override
            public MemberInfo parseToken(final String token) {
                if (token.equals("admin-token")) {
                    return new MemberInfo(1L, Role.ADMIN);
                }
                return new MemberInfo(1L, Role.BUYER);
            }

            @Override
            public void validateToken(final String token) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean isAccessToken(final String token) {
                throw new UnsupportedOperationException();
            }
        };
    }

}
