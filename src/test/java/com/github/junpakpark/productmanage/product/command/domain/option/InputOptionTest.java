package com.github.junpakpark.productmanage.product.command.domain.option;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import com.github.junpakpark.productmanage.product.command.domain.Money;
import com.github.junpakpark.productmanage.product.command.domain.Name;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class InputOptionTest {

    private InputOption sut;

    @BeforeEach
    void setUp() {
        // Arrange
        sut = new InputOption(
                new Name("추가 포장 서비스"),
                new Money(BigDecimal.valueOf(500))
        );
    }

    @Nested
    class UpdateChoices {

        @Test
        void emptyChoices() {
            // Arrange
            final List<OptionChoice> optionChoices = Collections.emptyList();

            // Action
            // Assert
            assertDoesNotThrow(() -> sut.replaceChoices(optionChoices));
        }

        @Test
        void nullChoices() {
            // Action
            // Assert
            assertDoesNotThrow(() -> sut.replaceChoices(null));
        }

        @Test
        void fail() {
            // Arrange
            final List<OptionChoice> optionChoices = List.of(new OptionChoice("빨강색"));

            // Action
            // Assert
            assertThatThrownBy(() -> sut.replaceChoices(optionChoices))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Input 옵션은 선택지를 변경할 수 없습니다.");
        }
    }

    @Test
    @DisplayName("InputOption은 빈 선택지 리스트를 반환한다")
    void optionChoices() {
        // Action
        final List<OptionChoice> choices = sut.optionChoices();

        // Assert
        assertThat(choices).isEmpty();
    }

}
