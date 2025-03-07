package com.github.junpakpark.productmanage.common.security.util;

import com.github.junpakpark.productmanage.common.security.exception.HeaderErrorCode;
import com.github.junpakpark.productmanage.common.security.exception.HeaderUnauthorizedException;
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
            throw new HeaderUnauthorizedException(HeaderErrorCode.AUTHORIZATION_HEADER_MISSING);
        }

        final String[] parts = authorization.split(" ");
        if (parts.length != 2 || !BEARER_PREFIX.equalsIgnoreCase(parts[0])) {
            throw new HeaderUnauthorizedException(HeaderErrorCode.AUTHORIZATION_HEADER_INVALID);
        }

        return parts[1];
    }

}
