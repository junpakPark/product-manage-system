package com.github.junpakpark.productmanage.common.security.adaptor.out.token;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import com.github.junpakpark.productmanage.common.domain.Role;
import com.github.junpakpark.productmanage.common.resolver.memberinfo.MemberInfo;
import com.github.junpakpark.productmanage.common.security.exception.TokenUnauthorizedException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
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
        @DisplayName("만료된 토큰 검증 시 TokenUnauthorizedException 발생")
        void validateExpiredToken() {
            // Arrange
            final String expiredToken = createToken(TokenType.ACCESS, -1000L, key);

            // Action & Assert
            assertThatThrownBy(() -> sut.validateToken(expiredToken))
                    .isInstanceOf(TokenUnauthorizedException.class)
                    .hasMessage("만료된 토큰입니다.");
        }

        @Test
        @DisplayName("서명이 잘못된 토큰 검증 시 TokenUnauthorizedException 발생")
        void validateInvalidSignatureToken() {
            // Arrange
            final Key differentKey = Keys.hmacShaKeyFor(
                    Decoders.BASE64.decode("differentSecretKeydifferentSecretKeydifferent")
            );
            final String token = createToken(TokenType.ACCESS, 3600000L, differentKey);

            // Action & Assert
            assertThatThrownBy(() -> sut.validateToken(token))
                    .isInstanceOf(TokenUnauthorizedException.class)
                    .hasMessage("유효하지 않은 토큰입니다.");
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
    class ValidateAccessTokenTest {

        @Test
        @DisplayName("AccessToken은 validateAccessToken 시 통과")
        void accessToken() {
            // Action
            // Assert
            assertDoesNotThrow(() -> sut.validateAccessToken(validAccessToken));
        }

        @Test
        @DisplayName("RefreshToken은 validateAccessToken 시 통과 예외 발생")
        void refreshToken() {
            // Action
            // Assert
            assertThatThrownBy(() -> sut.validateAccessToken(validRefreshToken))
                    .isInstanceOf(TokenUnauthorizedException.class)
                    .hasMessage("Access Token이 아닙니다.");
        }

        @Test
        @DisplayName("tokenType이 null인 경우 validateAccessToken 시 예외 발생")
        void tokenTypeNull() {
            // Arrange
            final String invalidToken = createTokenWithNullTokenType(3600000L, key);

            // Action
            // Assert
            assertThatThrownBy(() -> sut.validateAccessToken(invalidToken))
                    .isInstanceOf(TokenUnauthorizedException.class)
                    .hasMessageContaining("유효하지 않은 토큰입니다.");
        }

        @Test
        @DisplayName("tokenType이 없는 토큰은 validateAccessToken 시 예외 발생")
        void tokenTypeMissing() {
            // Arrange
            final String invalidToken = createTokenWithoutTokenType(3600000L, key);

            // Action
            // Assert
            assertThatThrownBy(() -> sut.validateAccessToken(invalidToken))
                    .isInstanceOf(TokenUnauthorizedException.class)
                    .hasMessageContaining("유효하지 않은 토큰입니다."); // 사용자 ID 부분은 동적일 수 있음
        }

        @Test
        @DisplayName("tokenType이 Enum에 정의되지 않은 경우 validateAccessToken 시 예외 발생")
        void unknownTokenType() {
            // Arrange
            final String invalidToken = createTokenWithUnknownTokenType(3600000L, key);

            // Action
            // Assert
            assertThatThrownBy(() -> sut.validateAccessToken(invalidToken))
                    .isInstanceOf(TokenUnauthorizedException.class)
                    .hasMessageContaining("유효하지 않은 토큰입니다.");
        }

        @Test
        @DisplayName("tokenType이 예상과 다른 타입(숫자 등)인 경우 validateAccessToken 시 예외 발생")
        void tokenTypeNotString() {
            final String invalidToken = createTokenWithNonStringTokenType(3600000L, key);
            // Action
            // Assert
            assertThatThrownBy(() -> sut.validateAccessToken(invalidToken))
                    .isInstanceOf(TokenUnauthorizedException.class)
                    .hasMessageContaining("유효하지 않은 토큰입니다.");
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

    private String createTokenWithNullTokenType(final long validityMillis, final Key key) {
        final Date now = new Date();
        final Date validity = new Date(now.getTime() + validityMillis);

        final Claims claims = Jwts.claims()
                .subject(String.valueOf(1L))
                .add("memberId", 1L)
                .add("role", Role.SELLER.name())
                .add("tokenType", null)
                .add("jti", UUID.randomUUID().toString())
                .build();

        return Jwts.builder()
                .claims(claims)
                .issuedAt(now)
                .expiration(validity)
                .signWith(key)
                .compact();
    }

    private String createTokenWithoutTokenType(final long validityMillis, final Key key) {
        final Date now = new Date();
        final Date validity = new Date(now.getTime() + validityMillis);

        final Claims claims = Jwts.claims()
                .subject(String.valueOf(1L))
                .add("memberId", 1L)
                .add("role", Role.SELLER.name())
                .add("jti", UUID.randomUUID().toString())
                .build();

        return Jwts.builder()
                .claims(claims)
                .issuedAt(now)
                .expiration(validity)
                .signWith(key)
                .compact();
    }


    private String createTokenWithUnknownTokenType(final long validityMillis, final Key key) {
        final Date now = new Date();
        final Date validity = new Date(now.getTime() + validityMillis);

        final Claims claims = Jwts.claims()
                .subject(String.valueOf(1L))
                .add("memberId", 1L)
                .add("role", Role.SELLER.name())
                .add("tokenType", "hello")
                .add("jti", UUID.randomUUID().toString())
                .build();

        return Jwts.builder()
                .claims(claims)
                .issuedAt(now)
                .expiration(validity)
                .signWith(key)
                .compact();
    }

    private String createTokenWithNonStringTokenType(final long validityMillis, final Key key) {
        final Date now = new Date();
        final Date validity = new Date(now.getTime() + validityMillis);

        final Claims claims = Jwts.claims()
                .subject(String.valueOf(1L))
                .add("memberId", 1L)
                .add("role", Role.SELLER.name())
                .add("tokenType", 1L)
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
