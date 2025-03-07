package com.github.junpakpark.productmanage.common.security.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.junpakpark.productmanage.common.domain.Role;
import com.github.junpakpark.productmanage.common.resolver.memberinfo.MemberInfo;
import com.github.junpakpark.productmanage.common.security.adaptor.out.token.JwtProperties;
import com.github.junpakpark.productmanage.common.security.adaptor.out.token.JwtTokenProvider;
import com.github.junpakpark.productmanage.common.security.adaptor.out.token.JwtTokenValidator;
import com.github.junpakpark.productmanage.common.security.application.dto.TokenPair;
import com.github.junpakpark.productmanage.common.security.application.port.out.persistence.RefreshTokenStore;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AuthServiceTest {

    private AuthService sut;
    private RefreshTokenStore refreshTokenStore;

    @BeforeEach
    void setUp() {
        final String secretKey = "mF9WkhY9L77NsWjN7a4aPPVVmYSHJZbXkHfH6Qh7yTg=";
        final JwtProperties jwtProperties = new JwtProperties(
                secretKey,
                3600000L,
                86400000L
        );
        refreshTokenStore = new FakeRefreshTokenStore();
        sut = new AuthService(new JwtTokenProvider(jwtProperties), new JwtTokenValidator(secretKey), refreshTokenStore);
    }

    @Test
    @DisplayName("accessToken과_refreshToken을_발급하고_refreshTokenStore에_저장한다")
    void issueTokens() {
        // Arrange
        final MemberInfo memberInfo = new MemberInfo(1L, Role.SELLER);

        // Act
        final TokenPair tokenPair = sut.issueTokens(memberInfo);

        // Assert
        final MemberInfo storedInfo = refreshTokenStore.findByRefreshToken(tokenPair.refreshToken());
        SoftAssertions.assertSoftly(softly -> {
            assertThat(tokenPair).isNotNull();
            assertThat(tokenPair.accessToken()).isNotNull();
            assertThat(tokenPair.refreshToken()).isNotNull();
            assertThat(storedInfo).isEqualTo(memberInfo);
        });
    }

    @Test
    @DisplayName("refreshToken으로_새로운_토큰을_재발급한다")
    void reissueToken() {
        // Arrange
        final MemberInfo memberInfo = new MemberInfo(1L, Role.SELLER);
        final TokenPair initialTokens = sut.issueTokens(memberInfo);

        // Act
        final TokenPair reissuedTokens = sut.reissueToken(initialTokens.refreshToken());

        // Assert
        SoftAssertions.assertSoftly(softly -> {
            assertThat(reissuedTokens).isNotNull();
            assertThat(reissuedTokens.accessToken()).isNotNull();
            assertThat(reissuedTokens.refreshToken()).isNotNull();
            assertThat(reissuedTokens.refreshToken()).isNotEqualTo(initialTokens.refreshToken());
        });
    }

    @Test
    @DisplayName("refreshToken을_제거한다")
    void removeRefreshToken() {
        // Arrange
        final MemberInfo memberInfo = new MemberInfo(1L, Role.ADMIN);
        final TokenPair tokenPair = sut.issueTokens(memberInfo);
        final String refreshToken = tokenPair.refreshToken();

        // Act
        sut.removeRefreshToken(refreshToken);

        // Assert
        assertThat(refreshTokenStore.findByRefreshToken(refreshToken)).isNull();
    }

}
