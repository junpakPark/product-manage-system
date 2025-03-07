package com.github.junpakpark.productmanage.common.security.application.port.out.token;

import com.github.junpakpark.productmanage.common.resolver.memberinfo.MemberInfo;

public interface TokenProvider {

    String createRefreshToken(final Long memberId);

    String createAccessToken(final MemberInfo memberInfo);
}
