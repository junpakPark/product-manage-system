package com.github.junpakpark.productmanage.product.command.domain.option;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.github.junpakpark.productmanage.product.command.domain.Money;
import com.github.junpakpark.productmanage.product.command.domain.Name;
import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class ProductOptionsTest {

    private ProductOptions sut;

    @BeforeEach
    void setUp() {
        sut = new ProductOptions();
    }

    @Nested
    class AddTests {

        @Test
        @DisplayName("옵션은 최대 3개까지 정상적으로 추가할 수 있다")
        void success() {
            // Act
            final ProductOption option1 = createOption(1L);
            final ProductOption option2 = createOption(2L);
            final ProductOption option3 = createOption(3L);
            sut.add(option1);
            sut.add(option2);
            sut.add(option3);

            // Assert
            assertThat(sut.getOptions()).hasSize(3)
                    .containsExactly(option1, option2, option3);
        }

        @Test
        @DisplayName("옵션이 3개를 초과하면 예외가 발생한다")
        void fail_when_over_max_options() {
            // Arrange
            sut.add(createOption(1L));
            sut.add(createOption(2L));
            sut.add(createOption(3L));

            final ProductOption extraOption = createOption(4L);

            // Act & Assert
            assertThatThrownBy(() -> sut.add(extraOption))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("옵션은 3개를 초과할 수 없습니다.");
        }

        @Test
        @DisplayName("동일한 이름의 옵션 추가 시 예외가 발생한다")
        void fail_when_duplicate_name() {
            // Arrange
            final ProductOption option1 = createOption(1L, "같은이름");
            final ProductOption option2 = createOption(2L, "같은이름");

            sut.add(option1);

            // Action
            // Assert
            assertThatThrownBy(() -> sut.add(option2))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("동일한 이름의 옵션이 이미 존재합니다.");
        }

    }

    @Nested
    class RemoveTests {

        @Test
        @DisplayName("옵션을 ID로 삭제할 수 있다")
        void success() {
            // Arrange
            final long optionId = 1L;
            final ProductOption option = createOption(optionId);
            sut.add(option);

            // Action
            sut.removeById(optionId);

            // Assert
            assertThat(sut.getOptions()).isEmpty();
        }

        @Test
        @DisplayName("존재하지 않는 옵션 ID 삭제 시 예외 발생")
        void fail() {
            // Arrange
            final long invalidId = 999L;

            // Action
            // Assert
            assertThatThrownBy(() -> sut.removeById(invalidId))
                    .isInstanceOf(NoSuchElementException.class);
        }
    }

    @Nested
    class UpdateTests {

        @Test
        @DisplayName("옵션을 ID로 수정할 수 있다")
        void success() {
            // Arrange
            final ProductOption option = createOption(1L);
            sut.add(option);

            final String updateName = "수정된 옵션명";
            final ProductOption updatedOption = createOption(1L, updateName);

            // Act
            sut.update(1L, updatedOption);

            // Assert
            final ProductOption result = sut.getOptions().get(0);
            assertThat(result.getName()).isEqualTo(new Name(updateName));
        }

        @Test
        @DisplayName("존재하지 않는 옵션 ID 수정 시 예외 발생")
        void fail_when_not_exist_id() {
            // Arrange
            final long invalidId = 999L;
            final ProductOption updatedOption = createOption(invalidId);

            // Act & Assert
            assertThatThrownBy(() -> sut.update(invalidId, updatedOption))
                    .isInstanceOf(NoSuchElementException.class);
        }

        @Test
        @DisplayName("업데이트 시 동일한 이름의 옵션이 이미 존재하면 예외 발생")
        void fail_when_duplicate_name() {
            // Arrange
            final ProductOption option1 = createOption(1L, "옵션A");
            final ProductOption option2 = createOption(2L, "옵션B");
            sut.add(option1);
            sut.add(option2);

            final ProductOption updatedOption = createOption(2L, "옵션A");

            // Act & Assert
            assertThatThrownBy(() -> sut.update(2L, updatedOption))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("동일한 이름의 옵션이 이미 존재합니다.");
        }

    }

    @Test
    @DisplayName("옵션 리스트는 ID 오름차순으로 반환된다")
    void getOptionsSortedById() {
        // Arrange
        final ProductOption option1 = createOption(1L);
        final ProductOption option2 = createOption(2L);
        sut.add(option2);
        sut.add(option1);

        // Act
        final List<ProductOption> result = sut.getOptions();

        // Assert
        assertThat(result).containsExactly(option1, option2);
    }

    private ProductOption createOption(final Long id) {
        return createOption(id, "옵션명" + id);
    }

    private ProductOption createOption(final Long id, final String name) {
        final ProductOption option = new InputOption(new Name(name), new Money(BigDecimal.valueOf(1000)));
        ReflectionTestUtils.setField(option, "id", id);
        return option;
    }

}
