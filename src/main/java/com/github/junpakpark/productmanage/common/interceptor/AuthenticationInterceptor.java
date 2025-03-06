package com.github.junpakpark.productmanage.common.interceptor;

import com.github.junpakpark.productmanage.common.security.application.port.out.token.TokenValidator;
import com.github.junpakpark.productmanage.common.security.util.AuthorizationHeaderExtractor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.naming.AuthenticationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthenticationInterceptor implements HandlerInterceptor {

    private final TokenValidator tokenValidator;

    @Override
    public boolean preHandle(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final Object handler
    ) throws AuthenticationException {
        if (isExcludedGetRequest(request)) {
            return true;
        }

        final String token = AuthorizationHeaderExtractor.extractToken(request);
        tokenValidator.validateToken(token);

        if (!tokenValidator.isAccessToken(token)) {
            log.warn(
                    "[SECURITY EVENT]: AccessToken이 아닌 토큰 - 요청 IP: {}, 요청 URL: {}",
                    request.getRemoteAddr(),
                    request.getRequestURI()
            );
            throw new AuthenticationException();
        }

        return true;
    }

    private boolean isExcludedGetRequest(HttpServletRequest request) {
        return "GET".equals(request.getMethod()) && request.getRequestURI().startsWith("/api/products");
    }
}
