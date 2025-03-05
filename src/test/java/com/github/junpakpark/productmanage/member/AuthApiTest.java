package com.github.junpakpark.productmanage.member;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.junpakpark.productmanage.ApiTest;
import com.github.junpakpark.productmanage.common.security.application.dto.TokenPair;
import io.restassured.http.Cookie;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
class AuthApiTest extends ApiTest {

    @Test
    @DisplayName("로그인 시 AccessToken과 RefreshToken을 반환한다")
    void login() {
        // Arrange
        MemberSteps.멤버가입요청(MemberSteps.멤버가입요청_생성());
        final var request = AuthSteps.로그인요청_생성();

        // Action
        final var response = AuthSteps.로그인요청(request);

        // Assert
        final String accessToken = response.jsonPath().getString("accessToken");
        final String refreshToken = response.cookie("refresh-token");

        SoftAssertions.assertSoftly(softly -> {
            assertThat(response.statusCode()).isEqualTo(200);
            assertThat(accessToken).isNotBlank();
            assertThat(refreshToken).isNotBlank();
        });
    }

    @Test
    @DisplayName("로그아웃 시 RefreshToken 쿠키가 만료된다")
    void logout() {
        // Arrange
        MemberSteps.멤버가입요청(MemberSteps.멤버가입요청_생성());
        final TokenPair tokenPair = AuthSteps.토큰발급();

        // Action
        final var response = AuthSteps.로그아웃요청(tokenPair);

        // Assert
        final Cookie cookie = response.detailedCookie("refresh-token");
        final String refreshToken = cookie.getValue();
        SoftAssertions.assertSoftly(softly -> {
            assertThat(response.statusCode()).isEqualTo(204);
            assertThat(refreshToken).isEmpty();
            assertThat(cookie.getMaxAge()).isZero();
        });
    }

    @Test
    @DisplayName("RefreshToken으로 AccessToken을 재발급한다")
    void reissue() {
        // Arrange
        MemberSteps.멤버가입요청(MemberSteps.멤버가입요청_생성());
        final TokenPair tokenPair = AuthSteps.토큰발급();

        // Action
        var response = AuthSteps.토큰재발급요청(tokenPair);

        // Assert
        final String newAccessToken = response.jsonPath().getString("accessToken");
        final String newRefreshToken = response.cookie("refresh-token");
        SoftAssertions.assertSoftly(softly -> {
            assertThat(response.statusCode()).isEqualTo(200);
            assertThat(newAccessToken).isNotBlank();
            assertThat(newRefreshToken).isNotBlank();
        });
    }

}
