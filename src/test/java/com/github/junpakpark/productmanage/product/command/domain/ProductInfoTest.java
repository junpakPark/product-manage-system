package com.github.junpakpark.productmanage.product.command.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("ProductInfo 클래스 테스트")
class ProductInfoTest {

    @Nested
    class CreationTests {

        @Test
        @DisplayName("정상 값으로 생성 성공")
        void success() {
            // Arrange
            final Name name = new Name("상품명");
            final String description = "정상 설명";
            final Money price = new Money(BigDecimal.valueOf(1000));
            final Money shippingFee = new Money(BigDecimal.valueOf(500));

            // Action
            final ProductInfo productInfo = new ProductInfo(name, description, price, shippingFee);

            // Assert
            SoftAssertions.assertSoftly(softly -> {
                assertThat(productInfo.getName()).isEqualTo(name);
                assertThat(productInfo.getDescription()).isEqualTo(description);
                assertThat(productInfo.getPrice()).isEqualTo(price);
                assertThat(productInfo.getShippingFee()).isEqualTo(shippingFee);
            });
        }

        @Test
        @DisplayName("name이 null이면 예외 발생")
        void nullName() {
            // Arrange
            final String description = "정상 설명";
            final Money price = new Money(BigDecimal.valueOf(1000));
            final Money shippingFee = new Money(BigDecimal.valueOf(500));

            // Action
            // Assert
            assertThatThrownBy(() -> new ProductInfo(null, description, price, shippingFee))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("상품명은 필수입니다.");
        }

        @Test
        @DisplayName("price가 null이면 예외 발생")
        void nullPrice() {
            // Arrange
            final Name name = new Name("상품명");
            final String description = "정상 설명";
            final Money shippingFee = new Money(BigDecimal.valueOf(500));

            // Action
            // Assert
            assertThatThrownBy(() -> new ProductInfo(name, description, null, shippingFee))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("상품 가격은 필수입니다.");
        }

        @Test
        @DisplayName("shippingFee가 null이면 예외 발생")
        void nullShippingFee() {
            // Arrange
            final Name name = new Name("상품명");
            final String description = "정상 설명";
            final Money price = new Money(BigDecimal.valueOf(1000));

            // Action
            // Assert
            assertThatThrownBy(() -> new ProductInfo(name, description, price, null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("배송비는 필수입니다.");
        }

        @Test
        @DisplayName("description이 500자를 초과하면 예외 발생")
        void tooLongDescription() {
            // Arrange
            final Name name = new Name("상품명");
            final String longDescription = "a".repeat(501);
            final Money price = new Money(BigDecimal.valueOf(1000));
            final Money shippingFee = new Money(BigDecimal.valueOf(500));

            // Action
            // Assert
            assertThatThrownBy(() -> new ProductInfo(name, longDescription, price, shippingFee))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("상품 설명은 최대 500자까지 가능합니다.");
        }

    }

    @Nested
    class OtherTests {

        @Test
        @DisplayName("동일한 정보면 동등하다")
        void equals() {
            // Arrange
            final Name name = new Name("상품명");
            final String description = "설명";
            final Money price = new Money(BigDecimal.valueOf(1000));
            final Money shippingFee = new Money(BigDecimal.valueOf(500));

            final ProductInfo productInfo1 = new ProductInfo(name, description, price, shippingFee);
            final ProductInfo productInfo2 = new ProductInfo(name, description, price, shippingFee);

            // Action
            // Assert
            assertThat(productInfo1).isEqualTo(productInfo2)
                    .hasSameHashCodeAs(productInfo2);
        }

        @Test
        @DisplayName("다른 정보면 동등하지 않다")
        void notEquals() {
            // Arrange
            final Name name = new Name("상품명");
            final Name differentName = new Name("다른상품명");
            final String description = "설명";
            final Money price = new Money(BigDecimal.valueOf(1000));
            final Money shippingFee = new Money(BigDecimal.valueOf(500));

            final ProductInfo productInfo1 = new ProductInfo(name, description, price, shippingFee);
            final ProductInfo productInfo2 = new ProductInfo(differentName, description, price, shippingFee);

            // Action
            // Assert
            SoftAssertions.assertSoftly(softly -> {
                assertThat(productInfo1).isNotEqualTo(productInfo2);
                assertThat(productInfo1.hashCode()).isNotEqualTo(productInfo2.hashCode());
            });
        }

        @Test
        @DisplayName("toString 결과에 주요 정보가 포함된다")
        void toStringTest() {
            // Arrange
            final Name name = new Name("상품명");
            final String description = "설명";
            final Money price = new Money(BigDecimal.valueOf(1000));
            final Money shippingFee = new Money(BigDecimal.valueOf(500));
            final ProductInfo productInfo = new ProductInfo(name, description, price, shippingFee);

            // Action
            final String result = productInfo.toString();

            // Assert
            assertThat(result).contains("상품명", "설명", "1000", "500");
        }
    }

}
