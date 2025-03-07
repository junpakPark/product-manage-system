package com.github.junpakpark.productmanage.common.security.application.port.in.web;

import com.github.junpakpark.productmanage.common.resolver.memberinfo.MemberInfo;
import com.github.junpakpark.productmanage.common.security.application.dto.TokenPair;

public interface AuthUseCase {

    TokenPair issueTokens(final MemberInfo memberInfo);

    TokenPair reissueToken(final String refreshToken);

    void removeRefreshToken(String refreshToken);

}
