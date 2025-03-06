package com.github.junpakpark.productmanage.product.command.domain.option;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class OptionTypeTest {

    @ParameterizedTest
    @CsvSource({
            "INPUT, INPUT, false",
            "SELECT, SELECT, false",
            "INPUT, SELECT, true",
            "SELECT, INPUT, true"
    })
    @DisplayName("같은 타입이면 참, 다른 타입이면 거짓을 반환한다.")
    void isDifferent(final OptionType first, final OptionType second, final boolean expected) {
        // Action
        boolean isDifferent = first.isDifferent(second);

        // Assert
        assertThat(isDifferent).isEqualTo(expected);
    }

}
