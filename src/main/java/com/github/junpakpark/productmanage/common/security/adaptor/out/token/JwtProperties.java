package com.github.junpakpark.productmanage.common.security.adaptor.out.token;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jwt.token")
public record JwtProperties(
        String secret,
        Long accessExpirationMs,
        Long refreshExpirationMs
) {
}
