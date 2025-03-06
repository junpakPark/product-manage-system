package com.github.junpakpark.productmanage.product.command.domain.option;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.github.junpakpark.productmanage.product.command.application.port.in.web.OptionChoiceCommand;
import com.github.junpakpark.productmanage.product.command.application.port.in.web.OptionCommand;
import com.github.junpakpark.productmanage.product.command.domain.Money;
import com.github.junpakpark.productmanage.product.command.domain.Name;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class OptionFactoryTest {

    private OptionFactory sut;

    @BeforeEach
    void setUp() {
        sut = new OptionFactory();
    }

    @Test
    @DisplayName("InputOption을 정상적으로 생성할 수 있다")
    void inputOption() {
        // Arrange
        final OptionCommand command = new OptionCommand(
                "문구 입력 옵션",
                OptionType.INPUT,
                BigDecimal.valueOf(1000),
                Collections.emptyList()
        );

        // Action
        final ProductOption option = sut.createOption(command);

        // Assert
        SoftAssertions.assertSoftly(softly -> {
            assertThat(option.getOptionType()).isEqualTo(OptionType.INPUT);
            assertThat(option.getName()).isEqualTo(new Name("문구 입력 옵션"));
            assertThat(option.getAdditionalPrice()).isEqualTo(new Money(BigDecimal.valueOf(1000)));
            assertThat(option.optionChoices()).isEmpty();
        });
    }

    @Test
    @DisplayName("SelectOption을 정상적으로 생성할 수 있다")
    void selectOption() {
        // Arrange
        final OptionCommand command = new OptionCommand(
                "색상 선택 옵션",
                OptionType.SELECT,
                BigDecimal.valueOf(2000),
                List.of(
                        new OptionChoiceCommand("빨강"),
                        new OptionChoiceCommand("파랑")
                )
        );

        // Action
        final ProductOption option = sut.createOption(command);

        // Assert
        final List<OptionChoice> choices = option.optionChoices();
        SoftAssertions.assertSoftly(softly -> {
            assertThat(option.getOptionType()).isEqualTo(OptionType.SELECT);
            assertThat(option.getName()).isEqualTo(new Name("색상 선택 옵션"));
            assertThat(option.getAdditionalPrice()).isEqualTo(new Money(BigDecimal.valueOf(2000)));
            assertThat(choices).hasSize(2)
                    .extracting(OptionChoice::getValue)
                    .containsExactly("빨강", "파랑");
        });
    }

    @Test
    @DisplayName("SelectOption 생성 시 선택지가 없으면 예외가 발생한다")
    void fail() {
        // Arrange
        final OptionCommand command = new OptionCommand(
                "색상 선택 옵션",
                OptionType.SELECT,
                BigDecimal.valueOf(2000),
                Collections.emptyList()
        );

        // Action
        // Assert
        assertThatThrownBy(() -> sut.createOption(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("선택지가 적어도 하나는 있어야합니다.");
    }

}
