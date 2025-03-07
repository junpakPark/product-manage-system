package com.github.junpakpark.productmanage.common.security.adaptor.out.token;

import com.github.junpakpark.productmanage.common.domain.Role;
import com.github.junpakpark.productmanage.common.resolver.memberinfo.MemberInfo;
import com.github.junpakpark.productmanage.common.security.application.port.out.token.TokenValidator;
import com.github.junpakpark.productmanage.common.security.exception.TokenErrorCode;
import com.github.junpakpark.productmanage.common.security.exception.TokenUnauthorizedException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtTokenValidator implements TokenValidator {

    private final JwtParser parser;

    public JwtTokenValidator(@Value("${jwt.token.secret}") final String secretKey) {
        this.parser = Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey)))
                .build();
    }

    @Override
    public void validateToken(final String token) {
        extractClaims(token);
    }

    @Override
    public MemberInfo parseToken(final String token) {
        final Claims claims = extractClaims(token);
        final Long memberId = Long.valueOf(claims.getSubject());
        final Role role = Role.valueOf(claims.get("role").toString());

        return new MemberInfo(memberId, role);
    }

    @Override
    public void validateAccessToken(final String token) {
        final Claims claims = extractClaims(token);
        TokenType tokenType;
        try {
            tokenType = TokenType.valueOf(claims.get("tokenType").toString());
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new TokenUnauthorizedException(TokenErrorCode.INVALID_TOKEN, claims.getSubject());
        }
        if (!tokenType.isAccess()) {
            throw new TokenUnauthorizedException(TokenErrorCode.NOT_ACCESS_TOKEN);
        }
    }

    private Claims extractClaims(final String token) {
        try {
            return parser.parseSignedClaims(token).getPayload();
        } catch (ExpiredJwtException e) {
            log.warn("Expired JWT token", e);
            throw new TokenUnauthorizedException(TokenErrorCode.EXPIRED_TOKEN);
        } catch (Exception e) {
            log.error("Invalid JWT token", e);
            throw new TokenUnauthorizedException(TokenErrorCode.INVALID_TOKEN);
        }
    }

}
