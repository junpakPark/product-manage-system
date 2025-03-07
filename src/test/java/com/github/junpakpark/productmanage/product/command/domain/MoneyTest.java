package com.github.junpakpark.productmanage.product.command.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.github.junpakpark.productmanage.product.exception.ProductBadRequestException;
import com.github.junpakpark.productmanage.product.exception.ProductErrorCode;
import java.math.BigDecimal;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class MoneyTest {

    @Nested
    class CreationTests {

        @Test
        @DisplayName("금액 생성 시 소수점 2자리로 반올림되어 저장된다.")
        void success() {
            // Arrange
            final BigDecimal amount = BigDecimal.valueOf(123.456);

            // Action
            final Money money = new Money(amount);

            // Assert
            assertThat(money.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(123.46));
        }

        @Test
        @DisplayName("금액이 null인 경우 예외가 발생한다.")
        void shouldThrowExceptionWhenAmountIsNull() {
            // Arrange
            final BigDecimal amount = null;

            // Action
            // Assert
            assertThatThrownBy(() -> new Money(amount))
                    .isInstanceOf(ProductBadRequestException.class)
                    .hasMessage(ProductErrorCode.PRICE_BAD_REQUEST.getMessage());
        }

        @Test
        @DisplayName("금액이 음수인 경우 예외가 발생한다.")
        void shouldThrowExceptionWhenAmountIsNegative() {
            // Arrange
            final BigDecimal amount = BigDecimal.valueOf(-1.00);

            // Action
            // Assert
            assertThatThrownBy(() -> new Money(amount))
                    .isInstanceOf(ProductBadRequestException.class)
                    .hasMessage(ProductErrorCode.PRICE_NEGATIVE_BAD_REQUEST.getMessage());
        }
    }

    @Nested
    class ArithmeticTests {

        @Test
        @DisplayName("두 금액을 더하면, 각 금액의 합이 반환된다.")
        void add() {
            // Arrange
            final Money money1 = new Money(BigDecimal.valueOf(100.123));
            final Money money2 = new Money(BigDecimal.valueOf(200.456));

            // Action
            final Money result = money1.add(money2);

            // Assert
            assertThat(result.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(300.58));
        }

        @Test
        @DisplayName("두 금액을 빼면, 각 금액의 차가 반환된다.")
        void subtractSuccess() {
            // Arrange
            final Money money1 = new Money(BigDecimal.valueOf(500.00));
            final Money money2 = new Money(BigDecimal.valueOf(200.456));

            // Action
            final Money result = money1.subtract(money2);

            // Assert
            assertThat(result.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(299.54));
        }

        @Test
        @DisplayName("뺄셈 결과가 0 이하인 경우 예외가 발생한다.")
        void subtractFail() {
            // Arrange
            final Money money1 = new Money(BigDecimal.valueOf(100.00));
            final Money money2 = new Money(BigDecimal.valueOf(200.00));

            // Action
            // Assert
            assertThatThrownBy(() -> money1.subtract(money2))
                    .isInstanceOf(ProductBadRequestException.class)
                    .hasMessage(ProductErrorCode.PRICE_NEGATIVE_BAD_REQUEST.getMessage());
        }
    }

    @Nested
    class EqualityTests {

        @Test
        @DisplayName("동일한 금액을 가진 두 Money 객체는 동등하다.")
        void sameAmount() {
            // Arrange
            final Money money1 = new Money(BigDecimal.valueOf(100.00));
            final Money money2 = new Money(BigDecimal.valueOf(100.00));

            // Act & Assert
            assertThat(money1).isEqualTo(money2)
                    .hasSameHashCodeAs(money2);
        }

        @Test
        @DisplayName("서로 다른 금액을 가진 두 Money 객체는 동등하지 않다.")
        void differentAmount() {
            // Arrange
            final Money money1 = new Money(new BigDecimal("100.00"));
            final Money money2 = new Money(new BigDecimal("101.00"));

            // Action
            // Assert
            SoftAssertions.assertSoftly(softly -> {
                assertThat(money1).isNotEqualTo(money2);
                assertThat(money1.hashCode()).isNotEqualTo(money2.hashCode());
            });
        }

    }

    @Nested
    @DisplayName("Other Tests")
    class OtherTests {

        @Test
        @DisplayName("ZERO 상수는 금액이 0원인 Money 객체이다.")
        void ZERO() {
            // Arrange
            final Money zero = Money.ZERO;

            // Act
            // Assert
            assertThat(zero.getAmount()).isEqualByComparingTo(BigDecimal.ZERO);
        }

        @Test
        @DisplayName("Money 객체는 문자열로 변환 시 소수점 2자리까지 표현된다.")
        void toStringTest() {
            // Arrange
            final Money money = new Money(BigDecimal.valueOf(123.456));

            // Act
            final String result = money.toString();

            // Assert
            assertThat(result).isEqualTo("123.46");
        }
    }

}
