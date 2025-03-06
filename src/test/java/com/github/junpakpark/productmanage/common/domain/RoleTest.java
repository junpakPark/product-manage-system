package com.github.junpakpark.productmanage.common.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;

class RoleTest {

    @ParameterizedTest
    @CsvSource({
            "ADMIN, true",
            "SELLER, false",
            "BUYER, false"
    })
    @DisplayName("ADMIN이면, true를 반환한다")
    void isAdmin(final Role role, final boolean expectedResult) {
        // Action
        final boolean result = role.isAdmin();

        // Assert
        assertThat(result).isEqualTo(expectedResult);
    }

    @Nested
    class ValidateSellerTest {

        @ParameterizedTest
        @EnumSource(value = Role.class, names = {"SELLER", "ADMIN"})
        @DisplayName("SELLER 또는 ADMIN은 validateSeller 통과")
        void validateSellerPasses(final Role role) {
            // Action
            // Assert
            assertDoesNotThrow(role::validateSeller);
        }

        @Test
        @DisplayName("BUYER는 validateSeller 시 예외 발생")
        void validateSellerThrowsForBuyer() {
            // Arrange
            final Role role = Role.BUYER;

            // Action
            // Assert
            assertThatThrownBy(role::validateSeller)
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("상품 관리 권한이 없습니다.");
        }

    }

}
