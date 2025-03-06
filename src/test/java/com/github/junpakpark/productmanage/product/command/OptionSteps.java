package com.github.junpakpark.productmanage.product.command;

import com.github.junpakpark.productmanage.common.security.application.dto.TokenPair;
import com.github.junpakpark.productmanage.product.command.application.port.in.web.OptionChoiceCommand;
import com.github.junpakpark.productmanage.product.command.application.port.in.web.OptionCommand;
import com.github.junpakpark.productmanage.product.command.domain.option.OptionType;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import org.springframework.http.MediaType;

@SuppressWarnings("NonAsciiCharacters")
public class OptionSteps {

    public static OptionCommand 입력옵션생성요청_생성() {
        return new OptionCommand(
                "원하시는 문구를 입력해주세요",
                OptionType.INPUT,
                BigDecimal.valueOf(1000),
                Collections.emptyList()
        );
    }

    public static OptionCommand 선택옵션생성요청_생성() {
        return new OptionCommand(
                "원하시는 색상을 선택해주세요",
                OptionType.SELECT,
                BigDecimal.valueOf(1000),
                List.of(
                        new OptionChoiceCommand("빨강"),
                        new OptionChoiceCommand("파랑")
                )
        );
    }

    public static ExtractableResponse<Response> 옵션생성요청(
            final TokenPair tokenPair,
            final Long productId,
            final OptionCommand request
    ) {
        return RestAssured.given().log().all()
                .header("Authorization", "Bearer " + tokenPair.accessToken())
                .cookie("refresh-token", tokenPair.refreshToken())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when()
                .post("/api/products/{productId}/options", productId)
                .then()
                .log().all()
                .extract();
    }

    public static OptionCommand 입력옵션수정요청_생성() {
        return new OptionCommand(
                "배달 시 요청사항을 입력해주세요",
                OptionType.INPUT,
                BigDecimal.valueOf(3000),
                Collections.emptyList()
        );
    }

    public static OptionCommand 선택옵션수정요청_생성() {
        return new OptionCommand(
                "원하시는 컬러를 선택해주세요",
                OptionType.SELECT,
                BigDecimal.valueOf(1000),
                List.of(
                        new OptionChoiceCommand("레드"),
                        new OptionChoiceCommand("블루")
                )
        );
    }

    public static ExtractableResponse<Response> 옵션수정요청(
            final TokenPair tokenPair,
            final Long productId,
            final Long optionId,
            final OptionCommand request
    ) {
        return RestAssured.given().log().all()
                .header("Authorization", "Bearer " + tokenPair.accessToken())
                .cookie("refresh-token", tokenPair.refreshToken())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when()
                .put("/api/products/{productId}/options/{optionId}", productId, optionId)
                .then()
                .log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 옵션삭제요청(
            final TokenPair tokenPair,
            final Long productId,
            final Long optionId
    ) {
        return RestAssured.given().log().all()
                .header("Authorization", "Bearer " + tokenPair.accessToken())
                .cookie("refresh-token", tokenPair.refreshToken())
                .when()
                .delete("/api/products/{productId}/options/{optionId}", productId, optionId)
                .then()
                .log().all()
                .extract();
    }
}
