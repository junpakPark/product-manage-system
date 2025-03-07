package com.github.junpakpark.productmanage.product.command.domain.option;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.github.junpakpark.productmanage.product.command.domain.Money;
import com.github.junpakpark.productmanage.product.command.domain.Name;
import com.github.junpakpark.productmanage.product.exception.OptionErrorCode;
import com.github.junpakpark.productmanage.product.exception.ProductBadRequestException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class SelectOptionTest {

    private SelectOption sut;
    private Name name;
    private Money additionalPrice;
    private List<OptionChoice> choices;

    @BeforeEach
    void setUp() {
        name = new Name("색상 선택");
        additionalPrice = new Money(BigDecimal.valueOf(1000));
        choices = List.of(
                new OptionChoice("파랑"),
                new OptionChoice("빨강")
        );
        sut = new SelectOption(name, additionalPrice, choices);
    }

    @Test
    @DisplayName("정상적으로 생성할 수 있다")
    void create() {
        // Assert
        SoftAssertions.assertSoftly(softly -> {
            assertThat(sut.getName()).isEqualTo(name);
            assertThat(sut.getAdditionalPrice()).isEqualTo(additionalPrice);
            assertThat(sut.optionChoices()).contains(choices.get(0), choices.get(1));
            assertThat(sut.getOptionType()).isEqualTo(OptionType.SELECT);
        });
    }

    @Nested
    class OptionChoicesTests {

        @Test
        @DisplayName("선택지를 교체하면 해당 선택지들이 저장된다")
        void updateChoices() {
            // Arrange
            choices = List.of(
                    new OptionChoice("파랑"),
                    new OptionChoice("빨강")
            );

            // Action
            sut.replaceChoices(choices);

            // Assert
            assertThat(sut.optionChoices())
                    .containsExactly(new OptionChoice("빨강"), new OptionChoice("파랑")); // 정렬 확인
        }

        @Test
        @DisplayName("선택지를 교체 시, null이면 예외가 발생한다")
        void nullChoices() {
            // Action
            // Assert
            assertThatThrownBy(() -> sut.replaceChoices(null))
                    .isInstanceOf(ProductBadRequestException.class)
                    .hasMessage(OptionErrorCode.SELECT_CHOICES_NULL_BAD_REQUEST.getMessage());
        }

        @Test
        @DisplayName("선택지를 업데이트 시, 빈 선택지면 예외가 발생한다")
        void emptyChoices() {
            // Arrange
            final List<OptionChoice> emptyChoices = Collections.emptyList();

            // Action
            // Assert
            assertThatThrownBy(() -> sut.replaceChoices(emptyChoices))
                    .isInstanceOf(ProductBadRequestException.class)
                    .hasMessage(OptionErrorCode.SELECT_CHOICES_EMPTY_BAD_REQUEST.getMessage());
        }
    }

}
