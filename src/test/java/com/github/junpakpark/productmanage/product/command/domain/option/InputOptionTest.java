package com.github.junpakpark.productmanage.product.command.domain.option;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import com.github.junpakpark.productmanage.product.command.domain.Money;
import com.github.junpakpark.productmanage.product.command.domain.Name;
import com.github.junpakpark.productmanage.product.exception.OptionErrorCode;
import com.github.junpakpark.productmanage.product.exception.ProductConflictException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class InputOptionTest {

    private InputOption sut;
    private Name name;
    private Money additionalPrice;

    @BeforeEach
    void setUp() {
        // Arrange
        name = new Name("원하시는 문구를 입력하세요");
        additionalPrice = new Money(BigDecimal.valueOf(500));
        sut = new InputOption(name, additionalPrice);
    }

    @Test
    @DisplayName("정상적으로 생성할 수 있다")
    void create() {
        // Assert
        SoftAssertions.assertSoftly(softly -> {
            assertThat(sut.getName()).isEqualTo(name);
            assertThat(sut.getAdditionalPrice()).isEqualTo(additionalPrice);
            assertThat(sut.getOptionType()).isEqualTo(OptionType.INPUT);
        });
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
                    .isInstanceOf(ProductConflictException.class)
                    .hasMessage(OptionErrorCode.INPUT_OPTION_CONFLICT.getMessage());
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
