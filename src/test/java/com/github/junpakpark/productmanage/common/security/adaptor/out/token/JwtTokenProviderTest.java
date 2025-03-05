package com.github.junpakpark.productmanage.common.security.adaptor.out.token;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.github.junpakpark.productmanage.common.domain.Role;
import com.github.junpakpark.productmanage.common.security.application.dto.MemberInfo;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import java.util.Date;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class JwtTokenProviderTest {

    private JwtTokenProvider sut;
    private JwtProperties jwtProperties;

    @BeforeEach
    void setUp() {
        jwtProperties = new JwtProperties(
                "mF9WkhY9L77NsWjN7a4aPPVVmYSHJZbXkHfH6Qh7yTg=",
                300000L,
                1800000L
        );
        sut = new JwtTokenProvider(jwtProperties);
    }

    @Test
    @DisplayName("AccessToken을 생성할 수 있다.")
    void createAccessToken() {
        // Arrange
        final MemberInfo memberInfo = new MemberInfo(1L, Role.SELLER);

        // Action
        final String accessToken = sut.createAccessToken(memberInfo);

        // Assert
        final Claims claims = parseClaims(accessToken);

        SoftAssertions.assertSoftly(softly -> {
            assertThat(claims.getSubject()).isEqualTo("1");
            assertThat(claims.get("role")).hasToString(Role.SELLER.name());
            assertThat(claims.get("tokenType")).hasToString(TokenType.ACCESS.name());
            assertExpirationWithinRange(claims, jwtProperties.accessExpirationMs());
        });
    }

    @Test
    @DisplayName("RefreshToken을 생성할 수 있다.")
    void createRefreshToken() {
        // Arrange
        final Long memberId = 1L;

        // Action
        final String refreshToken = sut.createRefreshToken(memberId);

        // Assert
        final Claims claims = parseClaims(refreshToken);

        SoftAssertions.assertSoftly(softly -> {
            assertThat(claims.getSubject()).isEqualTo("1");
            assertThat(claims.get("tokenType", String.class)).isEqualTo(TokenType.REFRESH.name());
            assertThat(claims.get("jti")).isNotNull();
            assertExpirationWithinRange(claims, jwtProperties.refreshExpirationMs());
        });
    }

    @Test
    @DisplayName("잘못된 시크릿으로 서명 검증 시 예외가 발생한다.")
    void invalidSignatureThrowsException() {
        // Arrange
        final JwtTokenProvider invalidProvider = new JwtTokenProvider(new JwtProperties(
                "wrongSecretKeyWrongSecretKeyWrongSecretKey=======",
                300000L,
                1800000L
        ));
        final MemberInfo memberInfo = new MemberInfo(1L, Role.SELLER);
        final String accessToken = invalidProvider.createAccessToken(memberInfo);


        // Act & Assert
        assertThatThrownBy(() -> parseClaims(accessToken))
                .isInstanceOf(SignatureException.class);
    }

    private Claims parseClaims(final String token) {
        return Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtProperties.secret())))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private void assertExpirationWithinRange(final Claims claims, final long expectedDurationMs) {
        Date issuedAt = claims.getIssuedAt();
        Date expiration = claims.getExpiration();

        long actualDuration = expiration.getTime() - issuedAt.getTime();

        assertThat(actualDuration)
                .isBetween(expectedDurationMs - 1000, expectedDurationMs + 1000);
    }
}
