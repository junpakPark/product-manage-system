package com.github.junpakpark.productmanage.acceptance.product;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.junpakpark.productmanage.acceptance.ApiTest;
import com.github.junpakpark.productmanage.acceptance.member.AuthSteps;
import com.github.junpakpark.productmanage.acceptance.member.MemberSteps;
import com.github.junpakpark.productmanage.common.security.application.dto.TokenPair;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
class OptionApiTest extends ApiTest {

    @Test
    @DisplayName("옵션을 정상적으로 추가하면 204 상태를 반환한다.")
    void addOption() {
        // Arrange
        MemberSteps.멤버가입요청(MemberSteps.멤버가입요청_생성());
        final TokenPair tokenPair = AuthSteps.토큰발급();
        final Long productId = getId(ProductSteps.상품생성요청(tokenPair, ProductSteps.상품생성요청_생성()));
        final var request = OptionSteps.입력옵션생성요청_생성();

        // Action
        final var response = OptionSteps.옵션생성요청(tokenPair, productId, request);

        // Assert
        assertThat(response.statusCode()).isEqualTo(204);
    }

    @Test
    @DisplayName("옵션을 정상적으로 수정하면 204 상태를 반환한다.")
    void updateOption() {
        // Arrange
        MemberSteps.멤버가입요청(MemberSteps.멤버가입요청_생성());
        final TokenPair tokenPair = AuthSteps.토큰발급();
        final Long productId = getId(ProductSteps.상품생성요청(tokenPair, ProductSteps.상품생성요청_생성()));
        OptionSteps.옵션생성요청(tokenPair, productId, OptionSteps.선택옵션생성요청_생성());
        final var request = OptionSteps.선택옵션수정요청_생성();

        // Action
        final var response = OptionSteps.옵션수정요청(tokenPair, productId, 1L, request);

        // Assert
        assertThat(response.statusCode()).isEqualTo(204);
    }

    @Test
    @DisplayName("옵션을 정상적으로 삭제하면 204 상태를 반환한다.")
    void deleteOption() {
        // Arrange
        MemberSteps.멤버가입요청(MemberSteps.멤버가입요청_생성());
        final TokenPair tokenPair = AuthSteps.토큰발급();
        final Long productId = getId(ProductSteps.상품생성요청(tokenPair, ProductSteps.상품생성요청_생성()));
        OptionSteps.옵션생성요청(tokenPair, productId, OptionSteps.입력옵션생성요청_생성());

        // Action
        final var response = OptionSteps.옵션삭제요청(tokenPair, productId, 1L);

        // Assert
        assertThat(response.statusCode()).isEqualTo(204);
    }
}
