package com.github.junpakpark.productmanage.common.security.adaptor.out.token;

import com.github.junpakpark.productmanage.common.security.application.dto.MemberInfo;
import com.github.junpakpark.productmanage.common.security.application.port.out.token.TokenValidator;
import com.github.junpakpark.productmanage.member.domain.Role;
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
        final Long memberId = Long.valueOf(claims.get("memberId").toString());
        final Role role = Role.valueOf(claims.get("role").toString());

        return new MemberInfo(memberId, role);
    }

    @Override
    public boolean isAccessToken(final String token) {
        final Claims claims = extractClaims(token);
        final TokenType tokenType = TokenType.valueOf(claims.get("tokenType").toString());

        return tokenType.isAccess();
    }

    private Claims extractClaims(final String token) {
        try {
            return parser.parseSignedClaims(token).getPayload();
        } catch (ExpiredJwtException e) {
            log.warn("Expired JWT token", e);
            throw e;
        } catch (Exception e) {
            log.error("Invalid JWT token", e);
            throw e;
        }
    }

}
