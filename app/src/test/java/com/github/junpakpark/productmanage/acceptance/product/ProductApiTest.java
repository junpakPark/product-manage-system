package com.github.junpakpark.productmanage.acceptance.product;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.junpakpark.productmanage.acceptance.ApiTest;
import com.github.junpakpark.productmanage.acceptance.member.AuthSteps;
import com.github.junpakpark.productmanage.acceptance.member.MemberSteps;
import com.github.junpakpark.productmanage.common.security.application.dto.TokenPair;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
class ProductApiTest extends ApiTest {

    @Test
    @DisplayName("상품을 정상적으로 생성하면 201 상태와 Location 헤더를 반환한다.")
    void createProduct() {
        // Arrange
        MemberSteps.멤버가입요청(MemberSteps.멤버가입요청_생성());
        final TokenPair tokenPair = AuthSteps.토큰발급();
        final var request = ProductSteps.상품생성요청_생성();

        // Action
        final var response = ProductSteps.상품생성요청(tokenPair, request);

        // Assert
        SoftAssertions.assertSoftly(softly -> {
            assertThat(response.statusCode()).isEqualTo(201);
            assertThat(response.header("Location")).isEqualTo(
                    String.format("http://localhost:%d/products/1", getPort()));
        });
    }

    @Test
    @DisplayName("상품 정보를 정상적으로 수정하면 204 상태를 반환한다.")
    void updateProduct() {
        // Arrange
        MemberSteps.멤버가입요청(MemberSteps.멤버가입요청_생성());
        final TokenPair tokenPair = AuthSteps.토큰발급();
        final Long productId = getId(ProductSteps.상품생성요청(tokenPair, ProductSteps.상품생성요청_생성()));
        final var request = ProductSteps.상품수정요청_생성();

        // Action
        final var response = ProductSteps.상품수정요청(tokenPair, productId, request);

        // Assert
        assertThat(response.statusCode()).isEqualTo(204);
    }

    @Test
    @DisplayName("상품을 정상적으로 삭제하면 204 상태를 반환한다.")
    void deleteProduct() {
        // Arrange
        MemberSteps.멤버가입요청(MemberSteps.멤버가입요청_생성());
        final TokenPair tokenPair = AuthSteps.토큰발급();
        final Long productId = getId(ProductSteps.상품생성요청(tokenPair, ProductSteps.상품생성요청_생성()));

        // Action
        final var response = ProductSteps.상품삭제요청(tokenPair, productId);

        // Assert
        assertThat(response.statusCode()).isEqualTo(204);
    }

}
