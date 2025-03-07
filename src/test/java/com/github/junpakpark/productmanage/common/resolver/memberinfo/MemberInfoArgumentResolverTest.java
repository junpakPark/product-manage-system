package com.github.junpakpark.productmanage.common.resolver.memberinfo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.github.junpakpark.productmanage.common.domain.Role;
import com.github.junpakpark.productmanage.common.security.application.port.out.token.TokenValidator;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

class MemberInfoArgumentResolverTest {

    private MemberInfoArgumentResolver sut;

    private MethodParameter methodParameter;
    private ModelAndViewContainer mavContainer;
    private NativeWebRequest webRequest;
    private WebDataBinderFactory binderFactory;
    private HttpServletRequest request;
    private final MemberInfo buyerInfo = new MemberInfo(1L, Role.BUYER);

    @BeforeEach
    void setUp() {
        sut = new MemberInfoArgumentResolver(getFakeTokenValidator());

        methodParameter = mock(MethodParameter.class);
        mavContainer = mock(ModelAndViewContainer.class);
        webRequest = mock(NativeWebRequest.class);
        request = mock(HttpServletRequest.class);
    }

    @Nested
    class SupportsParameter {

        @Test
        @DisplayName("AuthMember 애노테이션과 MemberInfo 타입을 가진 파라미터는 지원된다")
        void supportsParameter_withAuthMemberAnnotationAndMemberInfoType() {
            // Arrange
            when(methodParameter.hasParameterAnnotation(AuthMember.class)).thenReturn(true);
            when(methodParameter.getParameterType()).thenReturn((Class) MemberInfo.class);

            // Act
            final boolean result = sut.supportsParameter(methodParameter);

            // Assert
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("AuthMember 애노테이션이 없으면 지원하지 않는다")
        void supportsParameter_withoutAuthMemberAnnotation() {
            // Arrange
            when(methodParameter.hasParameterAnnotation(AuthMember.class)).thenReturn(false);
            when(methodParameter.getParameterType()).thenReturn((Class) MemberInfo.class);

            // Act
            final boolean result = sut.supportsParameter(methodParameter);

            // Assert
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("MemberInfo 타입이 아니면 지원하지 않는다")
        void supportsParameter_withNonMemberInfoType() {
            // Arrange
            when(methodParameter.hasParameterAnnotation(AuthMember.class)).thenReturn(true);
            when(methodParameter.getParameterType()).thenReturn((Class) String.class);

            // Act
            boolean result = sut.supportsParameter(methodParameter);

            // Assert
            assertThat(result).isFalse();
        }

    }

    @Nested
    class ResolveArgument {
        @Test
        @DisplayName("올바른 토큰이 있으면 MemberInfo를 반환한다")
        void resolveArgument_withValidToken() {
            // Arrange
            when(webRequest.getNativeRequest(HttpServletRequest.class)).thenReturn(request);
            when(request.getHeader("Authorization")).thenReturn("Bearer valid-token");

            // Act
            Object result = sut.resolveArgument(methodParameter, mavContainer, webRequest, binderFactory);

            // Assert
            assertThat(result).isInstanceOf(MemberInfo.class)
                    .isEqualTo(buyerInfo);
        }

        @Test
        @DisplayName("Authorization 헤더가 없으면 예외 발생")
        void resolveArgument_withoutAuthorizationHeader_throwsException() {
            // Arrange
            when(webRequest.getNativeRequest(HttpServletRequest.class)).thenReturn(request);
            when(request.getHeader("Authorization")).thenReturn(null);

            // Act & Assert
            assertThatThrownBy(() -> sut.resolveArgument(methodParameter, mavContainer, webRequest, binderFactory))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("Authorization 헤더가 잘못된 형식이면 예외 발생")
        void resolveArgument_withInvalidAuthorizationHeader_throwsException() {
            // Arrange
            when(webRequest.getNativeRequest(HttpServletRequest.class)).thenReturn(request);
            when(request.getHeader("Authorization")).thenReturn("Invalid-Token");

            // Act & Assert
            assertThatThrownBy(() -> sut.resolveArgument(methodParameter, mavContainer, webRequest, binderFactory))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    private TokenValidator getFakeTokenValidator() {
        return new TokenValidator() {
            @Override
            public MemberInfo parseToken(final String token) {
                if ("valid-token".equals(token)) {
                    return buyerInfo;
                }
                throw new IllegalArgumentException("Invalid token");
            }

            @Override
            public void validateToken(final String token) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void validateAccessToken(final String token) {
                throw new UnsupportedOperationException();
            }
        };
    }

}
