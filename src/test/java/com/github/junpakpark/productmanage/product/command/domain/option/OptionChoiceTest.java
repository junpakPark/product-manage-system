package com.github.junpakpark.productmanage.product.command.domain.option;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

class OptionChoiceTest {

    private OptionChoice sut;

    @BeforeEach
    void setUp() {
        // Arrange
        sut = new OptionChoice("빨강");
    }

    @Nested
    class CreationTests {

        @Test
        @DisplayName("정상적인 값으로 생성할 수 있다")
        void create() {
            // Assert
            assertThat(sut.getValue()).isEqualTo("빨강");
        }

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("값이 비어있거나 null이면 예외가 발생한다")
        void blankOrNullValue(final String value) {
            // Action
            // Assert
            assertThatThrownBy(() -> new OptionChoice(value))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("선택지는 비어있을 수 없습니다.");
        }

        @Test
        @DisplayName("최대 길이를 초과하면 예외가 발생한다")
        void tooLongValue() {
            // Arrange
            final String value = "a".repeat(31);

            // Action
            // Assert
            assertThatThrownBy(() -> new OptionChoice(value))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("선택지는 최대 30자 이하여야 합니다.");
        }
    }

    @Nested
    class OtherTests {

        @Test
        @DisplayName("동일한 값을 가진 두 객체는 동등하다.")
        void sameValue() {
            // Arrange
            final OptionChoice choice1 = new OptionChoice("파랑");
            final OptionChoice choice2 = new OptionChoice("파랑");

            // Action
            // Assert
            assertThat(choice1).isEqualTo(choice2)
                    .hasSameHashCodeAs(choice2);
        }

        @Test
        @DisplayName("다른 값을 가진 두 객체는 동등하지 않다다.")
        void differentValue() {
            // Arrange
            final OptionChoice choice2 = new OptionChoice("파랑");

            // Action
            // Assert
            SoftAssertions.assertSoftly(softly -> {
                assertThat(sut).isNotEqualTo(choice2);
                assertThat(sut.hashCode()).isNotEqualTo(choice2.hashCode());
            });
        }

        @Test
        @DisplayName("값이 포맷에 맞게 출력된다")
        void toStringTest() {
            // Action
            final String result = sut.toString();

            // Assert
            assertThat(result).isEqualTo("OptionChoice {빨강}");
        }
    }

}
