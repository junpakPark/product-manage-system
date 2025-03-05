package com.github.junpakpark.productmanage.common.interceptor;

import com.github.junpakpark.productmanage.common.resolver.memberinfo.MemberInfo;
import com.github.junpakpark.productmanage.common.security.application.port.out.token.TokenValidator;
import com.github.junpakpark.productmanage.common.security.util.AuthorizationHeaderExtractor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.nio.file.AccessDeniedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminAuthorizationInterceptor implements HandlerInterceptor {

    private final TokenValidator tokenValidator;

    @Override
    public boolean preHandle(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final Object handler
    ) throws Exception {
        final String token = AuthorizationHeaderExtractor.extractToken(request);
        final MemberInfo memberInfo = tokenValidator.parseToken(token);

        if (!memberInfo.role().isAdmin()) {
            log.warn(
                    "[SECURITY EVENT]: 관리자 권한이 없는 사용자 접근 - 요청 IP: {}, 요청 URL: {}",
                    request.getRemoteAddr(),
                    request.getRequestURI()
            );
            throw new AccessDeniedException("관리자 권한이 필요합니다.");
        }

        return true;
    }
}
