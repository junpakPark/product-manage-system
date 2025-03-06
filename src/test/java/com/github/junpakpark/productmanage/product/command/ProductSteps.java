package com.github.junpakpark.productmanage.product.command;

import com.github.junpakpark.productmanage.common.security.application.dto.TokenPair;
import com.github.junpakpark.productmanage.product.command.application.port.in.web.CreateProductCommand;
import com.github.junpakpark.productmanage.product.command.application.port.in.web.UpdateProductCommand;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.math.BigDecimal;
import org.springframework.http.MediaType;

@SuppressWarnings("NonAsciiCharacters")
public class ProductSteps {

    public static CreateProductCommand 상품생성요청_생성() {
        return new CreateProductCommand(
                "상품명",
                "상품 설명",
                BigDecimal.valueOf(1000L),
                BigDecimal.valueOf(100L)
        );
    }

    public static ExtractableResponse<Response> 상품생성요청(final TokenPair tokenPair, final CreateProductCommand request) {
        return RestAssured.given().log().all()
                .header("Authorization", "Bearer " + tokenPair.accessToken())
                .cookie("refresh-token", tokenPair.refreshToken())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when()
                .post("/api/products")
                .then()
                .log().all()
                .extract();
    }

    public static UpdateProductCommand 상품수정요청_생성() {
        return new UpdateProductCommand(
                "새상품명",
                "새로운 상품 설명",
                BigDecimal.valueOf(2000L),
                BigDecimal.valueOf(200L)
        );
    }

    public static ExtractableResponse<Response> 상품수정요청(
            final TokenPair tokenPair,
            final Long productId,
            final UpdateProductCommand request
    ) {
        return RestAssured.given().log().all()
                .header("Authorization", "Bearer " + tokenPair.accessToken())
                .cookie("refresh-token", tokenPair.refreshToken())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when()
                .put("/api/products/{productId}", productId)
                .then()
                .log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 상품삭제요청(final TokenPair tokenPair, final Long productId) {
        return RestAssured.given().log().all()
                .header("Authorization", "Bearer " + tokenPair.accessToken())
                .cookie("refresh-token", tokenPair.refreshToken())
                .when()
                .delete("/api/products/{productId}", productId)
                .then()
                .log().all()
                .extract();
    }

}
