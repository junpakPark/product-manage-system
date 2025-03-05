package com.github.junpakpark.productmanage.common.security.adaptor.out.token;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import com.github.junpakpark.productmanage.common.resolver.memberinfo.MemberInfo;
import com.github.junpakpark.productmanage.common.domain.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import java.security.Key;
import java.util.Date;
import java.util.UUID;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class JwtTokenValidatorTest {

    private JwtTokenValidator sut;
    private Key key;
    private String validAccessToken;
    private String validRefreshToken;

    @BeforeEach
    void setUp() {
        final String secretKey = "mF9WkhY9L77NsWjN7a4aPPVVmYSHJZbXkHfH6Qh7yTg=";
        key = Keys.hmacShaKeyFor(java.util.Base64.getDecoder().decode(secretKey));
        sut = new JwtTokenValidator(secretKey);
        validAccessToken = createToken(TokenType.ACCESS, 3600000L, key);
        validRefreshToken = createToken(TokenType.REFRESH, 3600000L, key);
    }

    @Nested
    class ValidToken {

        @Test
        @DisplayName("유효한 AccessToken은 정상적으로 검증된다.")
        void accessToken() {
            // Action & Assert
            assertDoesNotThrow(() -> sut.validateToken(validAccessToken));
        }

        @Test
        @DisplayName("유효한 RefreshToken은 정상적으로 검증된다.")
        void refreshToken() {
            // Action & Assert
            assertDoesNotThrow(() -> sut.validateToken(validRefreshToken));
        }

    }

    @Nested
    class InvalidToken {
        @Test
        @DisplayName("만료된 토큰 검증 시 ExpiredJwtException 발생")
        void validateExpiredToken() {
            // Arrange
            final String expiredToken = createToken(TokenType.ACCESS, -1000L, key);

            // Action & Assert
            assertThatThrownBy(() -> sut.validateToken(expiredToken))
                    .isInstanceOf(ExpiredJwtException.class);
        }

        @Test
        @DisplayName("서명이 잘못된 토큰 검증 시 SignatureException 발생")
        void validateInvalidSignatureToken() {
            // Arrange
            final Key differentKey = Keys.hmacShaKeyFor(
                    Decoders.BASE64.decode("differentSecretKeydifferentSecretKeydifferent")
            );
            final String token = createToken(TokenType.ACCESS, 3600000L, differentKey);

            // Action & Assert
            assertThatThrownBy(() -> sut.validateToken(token))
                    .isInstanceOf(SignatureException.class);
        }
    }

    @Test
    @DisplayName("AccessToken 파싱 시 올바른 MemberInfo 반환")
    void parseAccessToken() {
        // Action
        final MemberInfo memberInfo = sut.parseToken(validAccessToken);

        // Assert
        SoftAssertions.assertSoftly(softly -> {
            assertThat(memberInfo.memberId()).isEqualTo(1L);
            assertThat(memberInfo.role()).isEqualTo(Role.SELLER);
        });
    }

    @Nested
    class IsAccessToken {

        @Test
        @DisplayName("AccessToken은 isAccessToken()에서 true 반환")
        void accessToken() {
            // Action
            final boolean isAccessToken = sut.isAccessToken(validAccessToken);

            // Assert
            assertThat(isAccessToken).isTrue();
        }

        @Test
        @DisplayName("RefreshToken은 isAccessToken()에서 false 반환")
        void refreshToken() {
            // Action
            final boolean isAccessToken = sut.isAccessToken(validRefreshToken);

            // Assert
            assertThat(isAccessToken).isFalse();
        }

    }

    private String createToken(final TokenType tokenType, final long validityMillis, final Key key) {
        final Date now = new Date();
        final Date validity = new Date(now.getTime() + validityMillis);

        final Claims claims = Jwts.claims()
                .subject(String.valueOf(1L))
                .add("memberId", 1L)
                .add("role", Role.SELLER.name())
                .add("tokenType", tokenType.name())
                .add("jti", UUID.randomUUID().toString())
                .build();

        return Jwts.builder()
                .claims(claims)
                .issuedAt(now)
                .expiration(validity)
                .signWith(key)
                .compact();
    }

}
