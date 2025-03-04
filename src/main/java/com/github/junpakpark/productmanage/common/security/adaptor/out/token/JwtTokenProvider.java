package com.github.junpakpark.productmanage.common.security.adaptor.out.token;

import com.github.junpakpark.productmanage.member.domain.Role;
import com.github.junpakpark.productmanage.common.security.application.dto.MemberInfo;
import com.github.junpakpark.productmanage.common.security.application.port.out.token.TokenProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtTokenProvider implements TokenProvider {

    private final JwtProperties jwtProperties;
    private final Key key;

    public JwtTokenProvider(final JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtProperties.secret()));
    }

    @Override
    public String createAccessToken(final MemberInfo memberInfo) {
        final Claims claims = generateAccessClaims(memberInfo.memberId(), memberInfo.role());

        return createToken(claims, jwtProperties.accessExpirationMs());
    }

    @Override
    public String createRefreshToken(final Long memberId) {
        final Claims claims = generateRefreshClaims(memberId);

        return createToken(claims, jwtProperties.refreshExpirationMs());
    }

    private Claims generateAccessClaims(final Long memberId, Role role) {
        return Jwts.claims()
                .subject(String.valueOf(memberId))
                .add("role", role)
                .add("tokenType", TokenType.ACCESS.name())
                .build();
    }

    private Claims generateRefreshClaims(final Long memberId) {
        return Jwts.claims()
                .subject(String.valueOf(memberId))
                .add("jti", UUID.randomUUID().toString())
                .add("tokenType", TokenType.REFRESH.name())
                .build();
    }

    private String createToken(final Claims claims, final Long expirationTime) {
        final Date now = new Date();
        final Date validity = new Date(now.getTime() + expirationTime);

        return Jwts.builder()
                .signWith(key)
                .issuedAt(now)
                .expiration(validity)
                .claims(claims)
                .header().type("JWT")
                .and()
                .compact();
    }

}
