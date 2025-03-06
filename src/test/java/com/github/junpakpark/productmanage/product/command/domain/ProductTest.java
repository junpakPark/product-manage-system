package com.github.junpakpark.productmanage.product.command.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import com.github.junpakpark.productmanage.product.command.domain.option.InputOption;
import com.github.junpakpark.productmanage.product.command.domain.option.ProductOption;
import java.math.BigDecimal;
import java.util.NoSuchElementException;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.test.util.ReflectionTestUtils;

class ProductTest {

    private Product sut;
    private ProductInfo productInfo;
    private Long memberId;

    @BeforeEach
    void setUp() {
        // Arrange
        productInfo = createProductInfo();
        memberId = 1L;
        sut = new Product(productInfo, memberId);
    }

    @Nested
    class ConstructorTests {

        @Test
        @DisplayName("정상적인 값으로 생성할 수 있다.")
        void success() {
            // Assert
            SoftAssertions.assertSoftly(softly -> {
                assertThat(sut.getProductInfo()).isEqualTo(productInfo);
                assertThat(sut.getMemberId()).isEqualTo(memberId);
            });
        }

        @Test
        @DisplayName("productInfo가 null이면 예외가 발생한다.")
        void nullProductInfo() {
            // Action
            // Assert
            assertThatThrownBy(() -> new Product(null, memberId))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("상품 정보는 필수입니다.");
        }

        @Test
        @DisplayName("memberId가 null이면 예외가 발생한다.")
        void nullMemberId() {
            // Action
            // Assert
            assertThatThrownBy(() -> new Product(productInfo, null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("판매자 id는 필수입니다.");
        }

        @ParameterizedTest
        @ValueSource(longs = {0L, -1L})
        @DisplayName("memberId가 0이거나 음수이면 예외가 발생한다.")
        void zeroOrNegativeMemberId(final Long memberId) {
            // Action
            // Assert
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
            // Action
            // Assert
            assertDoesNotThrow(() -> sut.validateOwner(memberId));
        }

        @Test
        @DisplayName("다른 memberId면 예외가 발생한다.")
        void fail() {
            // Arrange
            final Long otherMemberId = 2L;

            // Action
            // Assert
            assertThatThrownBy(() -> sut.validateOwner(otherMemberId))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("상품 판매자 정보가 일치하지 않습니다. (productId= null)");
        }
    }

    @Nested
    class UpdateInfoTests {

        @Test
        @DisplayName("상품 정보를 새 정보로 변경할 수 있다.")
        void success() {
            // Arrange
            final ProductInfo newInfo = new ProductInfo(
                    new Name("새 상품명"),
                    "새 설명",
                    new Money(BigDecimal.valueOf(3000)),
                    new Money(BigDecimal.valueOf(150))
            );

            // Action
            sut.updateInfo(newInfo);

            // Assert
            assertThat(sut.getProductInfo()).isEqualTo(newInfo);
        }

        @Test
        @DisplayName("productInfo가 null이면 예외가 발생한다.")
        void fail() {
            // Action
            // Assert
            assertThatThrownBy(() -> sut.updateInfo(null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("상품 정보는 필수입니다.");
        }
    }

    @Nested
    class AddOptionTests {

        @Test
        @DisplayName("상품에 옵션을 추가할 수 있다")
        void success() {
            // Arrange
            final ProductOption option = createOption(1L);

            // Action
            sut.addOption(option);

            // Assert
            SoftAssertions.assertSoftly(softly -> {
                softly.assertThat(sut.getProductOptions().getOptions()).contains(option);
                softly.assertThat(option.getProduct()).isEqualTo(sut);
            });
        }

        @Test
        @DisplayName("다른 상품에 이미 연관된 옵션을 추가하려 하면 예외가 발생한다")
        void fail() {
            // Arrange
            final Product otherProduct = new Product(createProductInfo(), 2L);
            final ProductOption option = createOption(1L);
            otherProduct.addOption(option);

            // Act & Assert
            assertThatThrownBy(() -> sut.addOption(option))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("다른 상품의 옵션입니다.");
        }

    }

    @Nested
    class RemoveOptionTests {

        @Test
        @DisplayName("상품에서 옵션을 삭제할 수 있다")
        void success() {
            // Arrange
            final ProductOption option = createOption(1L);
            sut.addOption(option);

            // Action
            sut.removeOption(1L);

            // Assert
            SoftAssertions.assertSoftly(softly -> {
                softly.assertThat(sut.getProductOptions().getOptions()).isEmpty();
                softly.assertThat(option.getProduct()).isNull();
            });
        }

        @Test
        @DisplayName("존재하지 않는 옵션 삭제 시 예외가 발생한다")
        void fail() {
            // Arrange
            final long invalidOptionId = 999L;

            // Action
            // Assert
            assertThatThrownBy(() -> sut.removeOption(invalidOptionId))
                    .isInstanceOf(NoSuchElementException.class);
        }
    }


    @Nested
    class UpdateOptionTests {

        @Test
        @DisplayName("상품의 옵션을 수정할 수 있다")
        void success() {
            // Arrange
            final ProductOption option = createOption(1L, "원래 옵션명");
            sut.addOption(option);

            final ProductOption updatedOption = createOption(1L, "수정된 옵션명");

            // Action
            sut.updateOption(1L, updatedOption);

            // Assert
            final ProductOption result = sut.getProductOptions().getOptions().get(0);
            assertThat(result.getName()).isEqualTo(new Name("수정된 옵션명"));
        }

        @Test
        @DisplayName("존재하지 않는 옵션 수정 시 예외가 발생한다")
        void updateNonExistentOption() {
            // Arrange
            final long invalidOptionId = 999L;
            final ProductOption updatedOption = createOption(invalidOptionId);

            // Action
            // Assert
            assertThatThrownBy(() -> sut.updateOption(invalidOptionId, updatedOption))
                    .isInstanceOf(NoSuchElementException.class);
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

    private ProductOption createOption(final Long id) {
        final ProductOption option = new InputOption(new Name("옵션명" + id), new Money(BigDecimal.valueOf(500)));
        ReflectionTestUtils.setField(option, "id", id);
        return option;
    }

    private ProductOption createOption(final Long id, final String name) {
        final ProductOption option = new InputOption(new Name(name), new Money(BigDecimal.valueOf(500)));
        ReflectionTestUtils.setField(option, "id", id);
        return option;
    }

}
