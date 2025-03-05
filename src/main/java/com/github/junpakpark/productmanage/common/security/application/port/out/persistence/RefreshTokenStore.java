package com.github.junpakpark.productmanage.common.security.application.port.out.persistence;

import com.github.junpakpark.productmanage.common.resolver.memberinfo.MemberInfo;

public interface RefreshTokenStore {

    void save(final String refreshToken, final MemberInfo memberInfo);

    void remove(final String refreshToken);

    MemberInfo findByRefreshToken(final String refreshToken);

}
