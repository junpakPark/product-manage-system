package com.github.junpakpark.productmanage.member;

import com.github.junpakpark.productmanage.member.application.port.in.web.LoginCommand;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;

@SuppressWarnings("NonAsciiCharacters")
public class AuthSteps {

    public static ExtractableResponse<Response> 로그인요청(final LoginCommand request) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when()
                .post("/api/auth")
                .then()
                .log().all()
                .extract();
    }

    public static LoginCommand 로그인요청_생성() {
        return new LoginCommand("junpak.park@gmail.com", "password");
    }

    public static ExtractableResponse<Response> 로그아웃요청(final String issuedRefreshToken) {
        return RestAssured.given().log().all()
                .cookie("refresh-token", issuedRefreshToken)
                .when()
                .post("/api/auth/logout")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 토큰재발급요청(final String issuedRefreshToken) {
        return RestAssured.given().log().all()
                .cookie("refresh-token", issuedRefreshToken)
                .when()
                .post("/api/auth/reissue")
                .then().log().all()
                .extract();
    }
}
