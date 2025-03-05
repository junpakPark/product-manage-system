package com.github.junpakpark.productmanage.common.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class RoleTest {

    @ParameterizedTest
    @DisplayName("ADMIN이면, true를 반환한다")
    @CsvSource({
            "ADMIN, true",
            "SELLER, false",
            "BUYER, false"
    })
    void isAdmin(final Role role, final boolean expectedResult) {
        // Act
        final boolean result = role.isAdmin();

        // Assert
        assertThat(result).isEqualTo(expectedResult);
    }
}
