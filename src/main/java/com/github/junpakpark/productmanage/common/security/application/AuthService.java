package com.github.junpakpark.productmanage.common.security.application;

import com.github.junpakpark.productmanage.common.security.application.dto.MemberInfo;
import com.github.junpakpark.productmanage.common.security.application.dto.TokenPair;
import com.github.junpakpark.productmanage.common.security.application.port.in.web.AuthUseCase;
import com.github.junpakpark.productmanage.common.security.application.port.out.persistence.RefreshTokenStore;
import com.github.junpakpark.productmanage.common.security.application.port.out.token.TokenProvider;
import com.github.junpakpark.productmanage.common.security.application.port.out.token.TokenValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService implements AuthUseCase {

    private final TokenProvider tokenProvider;
    private final TokenValidator tokenValidator;
    private final RefreshTokenStore refreshTokenStore;

    @Override
    public TokenPair issueTokens(final MemberInfo memberInfo) {
        final String accessToken = tokenProvider.createAccessToken(memberInfo);

        final String refreshToken = tokenProvider.createRefreshToken(memberInfo.memberId());
        refreshTokenStore.save(refreshToken, memberInfo);

        return new TokenPair(accessToken, refreshToken);
    }

    @Override
    public TokenPair reissueToken(final String refreshToken) {
        tokenValidator.validateToken(refreshToken);
        final MemberInfo memberInfo = refreshTokenStore.findByRefreshToken(refreshToken);

        return issueTokens(memberInfo);
    }

    @Override
    public void removeRefreshToken(final String refreshToken) {
        tokenValidator.validateToken(refreshToken);
        refreshTokenStore.remove(refreshToken);
    }

}
