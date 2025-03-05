package com.github.junpakpark.productmanage.member.adapter.in.web;

import com.github.junpakpark.productmanage.common.security.application.dto.MemberInfo;
import com.github.junpakpark.productmanage.common.security.application.dto.TokenPair;
import com.github.junpakpark.productmanage.common.security.application.port.in.web.AuthUseCase;
import com.github.junpakpark.productmanage.member.adapter.in.web.dto.AccessTokenResponse;
import com.github.junpakpark.productmanage.member.application.port.in.web.LoginCommand;
import com.github.junpakpark.productmanage.member.application.port.in.web.ValidateMemberUseCase;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    public static final int REFRESH_TOKEN_EXPIRY_SECONDS = 14 * 24 * 60 * 60;

    private final AuthUseCase authUseCase;
    private final ValidateMemberUseCase validateMemberUseCase;

    @PostMapping
    public ResponseEntity<AccessTokenResponse> login(
            @Valid @RequestBody final LoginCommand request,
            final HttpServletResponse response
    ) {
        final MemberInfo memberInfo = validateMemberUseCase.validateMember(request);
        final TokenPair tokenPair = authUseCase.issueTokens(memberInfo);
        addRefreshTokenCookie(response, tokenPair.refreshToken(), REFRESH_TOKEN_EXPIRY_SECONDS);

        return ResponseEntity.ok(new AccessTokenResponse(tokenPair.accessToken()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @CookieValue("refresh-token") final String refreshToken,
            final HttpServletResponse response
    ) {
        authUseCase.removeRefreshToken(refreshToken);
        expireRefreshTokenCookie(response);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/reissue")
    public ResponseEntity<AccessTokenResponse> reissueTokens(
            @CookieValue("refresh-token") final String refreshToken,
            final HttpServletResponse response
    ) {
        final TokenPair tokenPair = authUseCase.reissueToken(refreshToken);
        addRefreshTokenCookie(response, tokenPair.refreshToken(), REFRESH_TOKEN_EXPIRY_SECONDS);

        return ResponseEntity.ok(new AccessTokenResponse(tokenPair.accessToken()));
    }

    private void expireRefreshTokenCookie(final HttpServletResponse response) {
        addRefreshTokenCookie(response, "", 0);
    }

    private void addRefreshTokenCookie(
            final HttpServletResponse response,
            final String refreshToken,
            final long maxAge
    ) {
        response.addHeader(
                HttpHeaders.SET_COOKIE,
                ResponseCookie.from("refresh-token", refreshToken)
                        .httpOnly(true)
                        .secure(true)
                        .sameSite("Strict")
                        .path("/")
                        .maxAge(maxAge)
                        .build()
                        .toString()
        );
    }

}
