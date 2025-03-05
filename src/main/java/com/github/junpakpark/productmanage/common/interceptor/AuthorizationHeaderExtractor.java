package com.github.junpakpark.productmanage.common.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

@Slf4j
public class AuthorizationHeaderExtractor {

    private static final String BEARER_PREFIX = "bearer";

    private AuthorizationHeaderExtractor() {
    }

    public static String extractToken(final HttpServletRequest request) {
        final String authorization = request.getHeader("Authorization");
        if (!StringUtils.hasText(authorization)) {
            logSecurityEvent(request, "Authorization 헤더 누락");
            throw new IllegalArgumentException("Authorization header is missing");
        }

        final String[] parts = authorization.split(" ");
        if (parts.length != 2 || !BEARER_PREFIX.equalsIgnoreCase(parts[0])) {
            logSecurityEvent(request, "Authorization 헤더 포맷 오류");
            throw new IllegalArgumentException("Authorization header is invalid");
        }

        return parts[1];
    }

    private static void logSecurityEvent(HttpServletRequest request, String message) {
        log.warn(
                "[SECURITY EVENT]: {} - 요청 IP: {}, 요청 URL: {}",
                message,
                request.getRemoteAddr(),
                request.getRequestURI()
        );
    }
}
