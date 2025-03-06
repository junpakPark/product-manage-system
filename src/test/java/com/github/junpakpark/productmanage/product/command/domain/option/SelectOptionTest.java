package com.github.junpakpark.productmanage.product.command.domain.option;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.github.junpakpark.productmanage.product.command.domain.Money;
import com.github.junpakpark.productmanage.product.command.domain.Name;
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

    @BeforeEach
    void setUp() {
        name = new Name("색상 선택");
        additionalPrice = new Money(BigDecimal.valueOf(1000));
        sut = new SelectOption(name, additionalPrice);
    }

    @Test
    @DisplayName("정상적으로 생성할 수 있다")
    void createSuccess() {
        // Assert
        SoftAssertions.assertSoftly(softly -> {
            assertThat(sut.getName()).isEqualTo(name);
            assertThat(sut.getAdditionalPrice()).isEqualTo(additionalPrice);
        });
    }

    @Nested
    class OptionChoicesTests {

        @Test
        @DisplayName("선택지를 교체하면 해당 선택지들이 저장된다")
        void updateChoices() {
            // Arrange
            final List<OptionChoice> choices = List.of(
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
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("optionValues는 null일 수 없습니다.");
        }

        @Test
        @DisplayName("선택지를 업데이트 시, 빈 선택지면 예외가 발생한다")
        void emptyChoices() {
            // Arrange
            final List<OptionChoice> choices = Collections.emptyList();

            // Action
            // Assert
            assertThatThrownBy(() -> sut.replaceChoices(choices))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("선택지가 적어도 하나는 있어야합니다.");
        }
    }

}
