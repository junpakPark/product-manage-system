package com.github.junpakpark.productmanage.member;

import com.github.junpakpark.productmanage.common.security.application.dto.TokenPair;
import com.github.junpakpark.productmanage.member.application.port.in.web.ChangePasswordCommand;
import com.github.junpakpark.productmanage.member.application.port.in.web.RegisterMemberCommand;
import com.github.junpakpark.productmanage.common.domain.Role;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;

@SuppressWarnings("NonAsciiCharacters")
public class MemberSteps {

    public static RegisterMemberCommand 멤버가입요청_생성() {
        return new RegisterMemberCommand(
                "박준현",
                "junpak.park@gmail.com",
                "password",
                Role.SELLER
        );
    }

    public static ExtractableResponse<Response> 멤버가입요청(final RegisterMemberCommand request) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when()
                .post("/api/members")
                .then()
                .log().all()
                .extract();
    }

    public static ChangePasswordCommand 비밀번호변경요청_생성() {
        return new ChangePasswordCommand("new-password");
    }

    public static ExtractableResponse<Response> 비밀번호변경요청(final TokenPair tokenPair, final ChangePasswordCommand request) {
        return RestAssured.given().log().all()
                .header("Authorization", "Bearer " + tokenPair.accessToken())
                .cookie("refresh-token", tokenPair.refreshToken())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when()
                .patch("/api/members/1/password")
                .then()
                .log().all()
                .extract();
    }
}
