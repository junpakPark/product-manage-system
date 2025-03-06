package com.github.junpakpark.productmanage.product.command.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.math.BigDecimal;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ProductTest {

    @Nested
    class ConstructorTests {

        @Test
        @DisplayName("정상적인 값으로 생성할 수 있다.")
        void success() {
            // Arrange
            final ProductInfo productInfo = createProductInfo();
            final Long memberId = 1L;

            // Action
            final Product product = new Product(productInfo, memberId);

            // Assert
            SoftAssertions.assertSoftly(softly -> {
                assertThat(product.getProductInfo()).isEqualTo(productInfo);
                assertThat(product.getMemberId()).isEqualTo(memberId);
            });
        }

        @Test
        @DisplayName("productInfo가 null이면 예외가 발생한다.")
        void nullProductInfo() {
            // Arrange
            final Long memberId = 1L;

            // Action
            // Assert
            assertThatThrownBy(() -> new Product(null, memberId))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("상품 정보는 필수입니다.");
        }

        @Test
        @DisplayName("memberId가 null이면 예외가 발생한다.")
        void nullMemberId() {
            // Arrange
            final ProductInfo productInfo = createProductInfo();

            // Act & Assert
            assertThatThrownBy(() -> new Product(productInfo, null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("판매자 id는 필수입니다.");
        }

        @ParameterizedTest
        @ValueSource(longs = {0L, -1L})
        @DisplayName("memberId가 0이거나 음수이면 예외가 발생한다.")
        void zeroOrNegativeMemberId(final Long memberId) {
            // Arrange
            final ProductInfo productInfo = createProductInfo();

            // Act & Assert
            assertThatThrownBy(() -> new Product(productInfo, memberId))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("id는 1보다 커야합니다.");
        }
    }

    @Nested
    class ValidateOwnerTests {

        @Test
        @DisplayName("같은 memberId면 검증을 통과한다.")
        void success() {
            // Arrange
            final ProductInfo productInfo = createProductInfo();
            final Long memberId = 1L;
            final Product product = new Product(productInfo, memberId);

            // Action
            // Assert
            assertDoesNotThrow(() -> product.validateOwner(memberId));
        }

        @Test
        @DisplayName("다른 memberId면 예외가 발생한다.")
        void fail() {
            // Arrange
            final ProductInfo productInfo = createProductInfo();
            final Long memberId = 1L;
            final Product product = new Product(productInfo, memberId);
            final Long otherMemberId = 2L;

            // Action
            // Assert
            assertThatThrownBy(() -> product.validateOwner(otherMemberId))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Owner id mismatch");
        }
    }

    @Nested
    class UpdateInfoTests {

        @Test
        @DisplayName("상품 정보를 새 정보로 변경할 수 있다.")
        void success() {
            // Arrange
            final ProductInfo initialInfo = createProductInfo();
            final ProductInfo newInfo = new ProductInfo(
                    new Name("새 상품명"),
                    "새 설명",
                    new Money(BigDecimal.valueOf(3000)),
                    new Money(BigDecimal.valueOf(150))
            );
            final Product product = new Product(initialInfo, 1L);

            // Action
            product.updateInfo(newInfo);

            // Assert
            assertThat(product.getProductInfo()).isEqualTo(newInfo);
        }

        @Test
        @DisplayName("productInfo가 null이면 예외가 발생한다.")
        void fail() {
            // Arrange
            final Product product = new Product(createProductInfo(), 1L);

            // Action
            // Assert
            assertThatThrownBy(() -> product.updateInfo(null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("상품 정보는 필수입니다.");
        }
    }

    private ProductInfo createProductInfo() {
        return new ProductInfo(
                new Name("상품명"),
                "상품 설명",
                new Money(BigDecimal.valueOf(1000)),
                new Money(BigDecimal.valueOf(100))
        );
    }
}
