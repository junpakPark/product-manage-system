package com.github.junpakpark.productmanage.common.security.application.port.out.token;

import com.github.junpakpark.productmanage.common.resolver.memberinfo.MemberInfo;

public interface TokenValidator {

    MemberInfo parseToken(final String token);

    void validateToken(final String token);

    void validateAccessToken(final String token);

}
