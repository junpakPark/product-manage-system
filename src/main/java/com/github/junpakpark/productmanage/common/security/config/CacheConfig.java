package com.github.junpakpark.productmanage.common.security.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.junpakpark.productmanage.common.security.application.dto.MemberInfo;
import java.time.Duration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public Cache<String, MemberInfo> refreshTokenCache() {
        return Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofDays(14L))
                .maximumSize(50000)
                .build();
    }

}
