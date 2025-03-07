package com.github.junpakpark.productmanage.common.security.application;

import com.github.junpakpark.productmanage.common.resolver.memberinfo.MemberInfo;
import com.github.junpakpark.productmanage.common.security.application.port.out.persistence.RefreshTokenStore;
import java.util.HashMap;
import java.util.Map;

public class FakeRefreshTokenStore implements RefreshTokenStore {

    private final Map<String, MemberInfo> store = new HashMap<>();

    @Override
    public void save(final String refreshToken, final MemberInfo memberInfo) {
        store.put(refreshToken, memberInfo);
    }

    @Override
    public void remove(final String refreshToken) {
        store.remove(refreshToken);
    }

    @Override
    public MemberInfo findByRefreshToken(final String refreshToken) {
        if (!store.containsKey(refreshToken)) {
            return null;
        }
        return store.get(refreshToken);
    }

}
