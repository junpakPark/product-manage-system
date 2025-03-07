package com.github.junpakpark.productmanage.common.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import com.github.junpakpark.productmanage.common.error.exception.ForbiddenException.RoleForbiddenException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class RoleTest {


    @Nested
    class ValidateAdminTest {

        @Test
        @DisplayName("관리자는 validateAdmin을 통과한다")
        void success() {
            // Arrange
            final Role role = Role.ADMIN;

            // Action
            // Assert
            assertDoesNotThrow(role::validateAdmin);
        }

        @ParameterizedTest
        @EnumSource(value = Role.class, names = {"SELLER", "BUYER"})
        @DisplayName("판매자 또는 구매자는 validateAdmin 시 예외 발생")
        void fail(final Role role) {
            // Action
            // Assert
            assertThatThrownBy(role::validateAdmin)
                    .isInstanceOf(RoleForbiddenException.class)
                    .hasMessageContaining("관리자 이상의 권한이 필요합니다.");
        }

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
                    .isInstanceOf(RoleForbiddenException.class)
                    .hasMessage("판매자 이상의 권한이 필요합니다.");
        }

    }

}
