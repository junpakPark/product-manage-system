package com.github.junpakpark.productmanage.common.security.adaptor.out.persistence;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.junpakpark.productmanage.common.resolver.memberinfo.MemberInfo;
import com.github.junpakpark.productmanage.common.security.application.port.out.persistence.RefreshTokenStore;
import org.springframework.stereotype.Component;

@Component
public class InMemoryRefreshTokenStore implements RefreshTokenStore {

    private final Cache<String, MemberInfo> refreshTokenCache;

    public InMemoryRefreshTokenStore(final Cache<String, MemberInfo> refreshTokenCache) {
        this.refreshTokenCache = refreshTokenCache;
    }

    @Override
    public void save(final String refreshToken, final MemberInfo memberInfo) {
        refreshTokenCache.put(refreshToken, memberInfo);
    }

    @Override
    public void remove(final String refreshToken) {
        refreshTokenCache.invalidate(refreshToken);
    }

    @Override
    public MemberInfo findByRefreshToken(final String refreshToken) {
        return refreshTokenCache.getIfPresent(refreshToken);
    }
}
