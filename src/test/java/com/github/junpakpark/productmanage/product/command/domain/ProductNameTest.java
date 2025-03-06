package com.github.junpakpark.productmanage.product.command.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;


class ProductNameTest {

    @Nested
    class CreationTests {

        @Test
        @DisplayName("정상적인 이름으로 객체 생성에 성공한다.")
        void success() {
            // Arrange
            final String validName = "Valid Product Name";

            // Action
            final ProductName productName = new ProductName(validName);

            // Assert
            assertThat(productName.getName()).isEqualTo(validName);
        }


        @Test
        @DisplayName("null 값으로 객체 생성 시 예외가 발생한다.")
        void nullName() {
            // Arrange
            final String nullName = null;

            // Action
            // Assert
            assertThatThrownBy(() -> new ProductName(nullName))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("상품명은 비어있을 수 없습니다.");
        }

        @Test
        @DisplayName("공백 값으로 객체 생성 시 예외가 발생한다.")
        void blankName() {
            // Arrange
            final String blankName = "    ";

            // Action
            // Assert
            assertThatThrownBy(() -> new ProductName(blankName))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("상품명은 비어있을 수 없습니다.");
        }

        @Test
        @DisplayName("100자를 초과하는 이름으로 객체 생성 시 예외가 발생한다.")
        void tooLongName() {
            // Arrange
            final String tooLongName = "a".repeat(101);

            // Action
            // Assert
            assertThatThrownBy(() -> new ProductName(tooLongName))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("상품명은 최대 100자 이하여야 합니다.");
        }

    }

    @Nested
    class EqualityTests {

        @Test
        @DisplayName("동일한 이름을 가진 두 객체는 동등하다.")
        void sameName() {
            // Arrange
            final ProductName name1 = new ProductName("Product A");
            final ProductName name2 = new ProductName("Product A");

            // Action
            // Assert
            assertThat(name1).isEqualTo(name2)
                    .hasSameHashCodeAs(name2);

        }

        @Test
        @DisplayName("서로 다른 이름을 가진 두 객체는 동등하지 않다.")
        void differentName() {
            // Arrange
            final ProductName name1 = new ProductName("Product A");
            final ProductName name2 = new ProductName("Product B");

            // Action
            // Assert
            SoftAssertions.assertSoftly(softly -> {
                assertThat(name1).isNotEqualTo(name2);
                assertThat(name1.hashCode()).isNotEqualTo(name2.hashCode());
            });
        }
    }

    @Test
    @DisplayName("toString은 상품명을 포함한 문자열을 반환한다.")
    void ToStringTest() {
        // Arrange
        final String name = "Sample Product";
        final ProductName productName = new ProductName(name);

        // Act
        final String result = productName.toString();

        // Assert
        assertThat(result).isEqualTo("ProductName= Sample Product");
    }
}
