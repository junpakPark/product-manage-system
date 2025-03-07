package com.github.junpakpark.productmanage.product.command.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import com.github.junpakpark.productmanage.product.command.domain.option.InputOption;
import com.github.junpakpark.productmanage.product.command.domain.option.ProductOption;
import com.github.junpakpark.productmanage.product.exception.OptionErrorCode;
import com.github.junpakpark.productmanage.product.exception.ProductBadRequestException;
import com.github.junpakpark.productmanage.product.exception.ProductConflictException;
import com.github.junpakpark.productmanage.product.exception.ProductErrorCode;
import com.github.junpakpark.productmanage.product.exception.ProductFobiddenException;
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
    private Long memberId;
    private Name name;
    private String description;
    private Money price;
    private Money shippingFee;

    @BeforeEach
    void setUp() {
        // Arrange
        memberId = 1L;
        name = new Name("상품명");
        description = "상품 설명";
        price = new Money(BigDecimal.valueOf(1000));
        shippingFee = new Money(BigDecimal.valueOf(100));
        sut = new Product(name, description, price, shippingFee, memberId);
    }

    @Nested
    class ConstructorTests {

        @Test
        @DisplayName("정상적인 값으로 생성할 수 있다.")
        void success() {
            // Assert
            SoftAssertions.assertSoftly(softly -> {
                assertThat(sut.getName()).isEqualTo(name);
                assertThat(sut.getDescription()).isEqualTo(description);
                assertThat(sut.getPrice()).isEqualTo(price);
                assertThat(sut.getShippingFee()).isEqualTo(shippingFee);
                assertThat(sut.getMemberId()).isEqualTo(memberId);
            });
        }

        @Test
        @DisplayName("name이 null이면 예외 발생")
        void nullName() {
            // Action
            // Assert
            assertThatThrownBy(() -> new Product(null, description, price, shippingFee, memberId))
                    .isInstanceOf(ProductBadRequestException.class)
                    .hasMessage(ProductErrorCode.NAME_BAD_REQUEST.getMessage());
        }

        @Test
        @DisplayName("price가 null이면 예외 발생")
        void nullPrice() {
            // Action
            // Assert
            assertThatThrownBy(() -> new Product(name, description, null, shippingFee, memberId))
                    .isInstanceOf(ProductBadRequestException.class)
                    .hasMessage(ProductErrorCode.PRICE_BAD_REQUEST.getMessage());
        }

        @Test
        @DisplayName("shippingFee가 null이면 예외 발생")
        void nullShippingFee() {
            // Action
            // Assert
            assertThatThrownBy(() -> new Product(name, description, price, null, memberId))
                    .isInstanceOf(ProductBadRequestException.class)
                    .hasMessage(ProductErrorCode.SHIPPING_FEE_BAD_REQUEST.getMessage());
        }

        @Test
        @DisplayName("description이 500자를 초과하면 예외 발생")
        void tooLongDescription() {
            // Arrange
            final String longDescription = "a".repeat(501);

            // Action
            // Assert
            assertThatThrownBy(() -> new Product(name, longDescription, price, shippingFee, memberId))
                    .isInstanceOf(ProductBadRequestException.class)
                    .hasMessage(ProductErrorCode.DESCRIPTION_BAD_REQUEST.getMessage());
        }

        @Test
        @DisplayName("memberId가 null이면 예외가 발생한다.")
        void nullMemberId() {
            // Action
            // Assert
            assertThatThrownBy(() -> new Product(name, description, price, shippingFee, null))
                    .isInstanceOf(ProductBadRequestException.class)
                    .hasMessage(ProductErrorCode.MEMBER_BAD_REQUEST.getMessage());
        }

        @ParameterizedTest
        @ValueSource(longs = {0L, -1L})
        @DisplayName("memberId가 0이거나 음수이면 예외가 발생한다.")
        void zeroOrNegativeMemberId(final Long memberId) {
            // Action
            // Assert
            assertThatThrownBy(() -> new Product(name, description, price, shippingFee, memberId))
                    .isInstanceOf(ProductBadRequestException.class)
                    .hasMessage(ProductErrorCode.MEMBER_BAD_REQUEST.getMessage());
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
                    .isInstanceOf(ProductFobiddenException.class)
                    .hasMessage(ProductErrorCode.PRODUCT_FORBIDDEN.getMessage() + " 회원 ID: 2");
        }
    }

    @Nested
    class UpdateInfoTests {

        @Test
        @DisplayName("상품 정보를 새 정보로 변경할 수 있다.")
        void success() {
            // Arrange
            final Name newName = new Name("새 상품명");
            final String newDescription = "새 설명";
            final Money newPrice = new Money(BigDecimal.valueOf(3000));
            final Money newShippingFee = new Money(BigDecimal.valueOf(150));
            final Product updatedProduct = new Product(newName, newDescription, newPrice, newShippingFee, memberId);

            // Action
            sut.update(updatedProduct);

            // Assert
            SoftAssertions.assertSoftly(softly -> {
                assertThat(sut.getName()).isEqualTo(newName);
                assertThat(sut.getDescription()).isEqualTo(newDescription);
                assertThat(sut.getPrice()).isEqualTo(newPrice);
                assertThat(sut.getShippingFee()).isEqualTo(newShippingFee);
            });
        }

        @Test
        @DisplayName("productInfo가 null이면 예외가 발생한다.")
        void fail() {
            // Action
            // Assert
            assertThatThrownBy(() -> sut.update(null))
                    .isInstanceOf(ProductBadRequestException.class)
                    .hasMessage(ProductErrorCode.UPDATE_BAD_REQUEST.getMessage());
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
                softly.assertThat(sut.getProductOptions()).contains(option);
                softly.assertThat(option.getProduct()).isEqualTo(sut);
            });
        }

        @Test
        @DisplayName("다른 상품에 이미 연관된 옵션을 추가하려 하면 예외가 발생한다")
        void fail() {
            // Arrange
            final Product otherProduct = new Product(name, description, price, shippingFee, 2L);
            final ProductOption option = createOption(1L);
            otherProduct.addOption(option);

            // Act & Assert
            assertThatThrownBy(() -> sut.addOption(option))
                    .isInstanceOf(ProductConflictException.class)
                    .hasMessage(OptionErrorCode.ASSOCIATION_CONFLICT.getMessage());
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
                softly.assertThat(sut.getProductOptions()).isEmpty();
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
            final ProductOption option = createOption("원래 옵션명");
            sut.addOption(option);

            final ProductOption updatedOption = createOption("수정된 옵션명");

            // Action
            sut.updateOption(1L, updatedOption);

            // Assert
            final ProductOption result = sut.getProductOptions().getFirst();
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

    private ProductOption createOption(final Long id) {
        final ProductOption option = new InputOption(new Name("옵션명" + id), new Money(BigDecimal.valueOf(500)));
        ReflectionTestUtils.setField(option, "id", id);
        return option;
    }

    private ProductOption createOption(final String name) {
        final ProductOption option = new InputOption(new Name(name), new Money(BigDecimal.valueOf(500)));
        ReflectionTestUtils.setField(option, "id", 1L);
        return option;
    }

}
