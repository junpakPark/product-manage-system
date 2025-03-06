package com.github.junpakpark.productmanage.product.command.domain.option;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.github.junpakpark.productmanage.product.command.domain.Money;
import com.github.junpakpark.productmanage.product.command.domain.Name;
import com.github.junpakpark.productmanage.product.command.domain.Product;
import java.math.BigDecimal;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class ProductOptionTest {

    private Name name;
    private Money price;
    private ProductOption sut;

    @BeforeEach
    void setUp() {
        // Arrange
        name = new Name("원하시는 문구를 입력하세요");
        price = new Money(BigDecimal.valueOf(1000));
        sut = new InputOption(name, price);
    }

    @Nested
    class ConstructorTests {

        @Test
        @DisplayName("정상적인 값으로 생성할 수 있다")
        void create() {
            // Assert
            SoftAssertions.assertSoftly(softly -> {
                assertThat(sut.getName()).isEqualTo(name);
                assertThat(sut.getAdditionalPrice()).isEqualTo(price);
            });
        }

        @Test
        @DisplayName("name이 null이면 예외가 발생한다")
        void nullName() {
            // Action
            // Assert
            assertThatThrownBy(() -> new InputOption(null, price))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("옵션명은 null이 될 수 없습니다.");
        }

        @Test
        @DisplayName("additionalPrice가 null이면 예외가 발생한다")
        void nullAdditionalPrice() {
            // Action
            // Assert
            assertThatThrownBy(() -> new InputOption(name, null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("추가 요금은 null이 될 수 없습니다.");
        }
    }

    @Nested
    class UpdateTests {

        @Test
        @DisplayName("이름과 추가 금액, 선택지를 업데이트할 수 있다")
        void update() {
            // Arrange
            final Name newName = new Name("변경된 옵션명");
            final Money newPrice = new Money(BigDecimal.valueOf(2000));
            final ProductOption updatedOption = new InputOption(newName, newPrice);

            // Action
            sut.update(updatedOption);

            // Assert
            SoftAssertions.assertSoftly(softly -> {
                softly.assertThat(sut.getName()).isEqualTo(newName);
                softly.assertThat(sut.getAdditionalPrice()).isEqualTo(newPrice);
                softly.assertThat(sut.optionChoices()).isEmpty();
            });
        }

    }

    @Nested
    class IsSameTests {

        @Test
        @DisplayName("동일 ID면 true를 반환한다")
        void success() {
            // Arrange
            final long optionId = 1L;
            ReflectionTestUtils.setField(sut, "id", optionId);

            // Action
            final boolean isSame = sut.hasSameId(optionId);

            // Assert
            assertThat(isSame).isTrue();
        }

        @Test
        @DisplayName("동일 ID가 아니면 false를 반환한다")
        void fail() {
            // Arrange
            final long optionId = 1L;
            ReflectionTestUtils.setField(sut, "id", optionId);

            // Action
            final boolean isSame = sut.hasSameId(2L);

            // Assert
            assertThat(isSame).isFalse();
        }
    }

    @Nested
    class AssociationTests {

        private Product product;

        @BeforeEach
        void setUp() {
            product = new Product(
                    new Name("상품명"),
                    "상품 설명",
                    new Money(BigDecimal.valueOf(1000)),
                    new Money(BigDecimal.valueOf(100)),
                    1L
            );
        }

        @Test
        @DisplayName("상품과 정상적으로 연관 설정할 수 있다")
        void associateWithProduct() {
            // Action
            sut.associatedWith(product);

            // Assert
            assertThat(sut.getProduct()).isEqualTo(product);
        }

        @Test
        @DisplayName("이미 연관된 상품이 있는 경우 예외가 발생한다")
        void alreadyAssociatedThrowsException() {
            // Arrange
            sut.associatedWith(product);
            final Product secondProduct = new Product(
                    new Name("다른 상품명"),
                    "다른 상품 설명",
                    new Money(BigDecimal.valueOf(1000)),
                    new Money(BigDecimal.valueOf(100)),
                    2L
            );

            // Action
            // Assert
            assertThatThrownBy(() -> sut.associatedWith(secondProduct))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("다른 상품의 옵션입니다.");
        }

        @Test
        @DisplayName("연관 관계를 정상적으로 해제할 수 있다")
        void breakAssociation() {
            // Arrange
            sut.associatedWith(product);

            // Act
            sut.breakAssociation();

            // Assert
            assertThat(sut.getProduct()).isNull();
        }
    }

    @Nested
    class EqualsAndHashCodeTests {

        @Test
        @DisplayName("동일 ID면 equals true")
        void sameId() {
            // Arrange
            ReflectionTestUtils.setField(sut, "id", 1L);
            final InputOption other = new InputOption(name, price);
            ReflectionTestUtils.setField(other, "id", 1L);

            // Assert
            assertThat(sut).isEqualTo(other)
                    .hasSameHashCodeAs(other);
        }

        @Test
        @DisplayName("ID가 다르면 equals false")
        void differentId() {
            // Arrange
            ReflectionTestUtils.setField(sut, "id", 1L);
            final InputOption other = new InputOption(name, price);
            ReflectionTestUtils.setField(other, "id", 2L);

            // Assert
            SoftAssertions.assertSoftly(softly -> {
                assertThat(sut).isNotEqualTo(other);
                assertThat(sut.hashCode()).isNotEqualTo(other.hashCode());
            });
        }

        @Test
        @DisplayName("ID가 null인 경우 equals false")
        void nullId() {
            // Arrange
            final InputOption other = new InputOption(name, price);

            // Assert
            SoftAssertions.assertSoftly(softly -> {
                assertThat(sut).isNotEqualTo(other);
                assertThat(sut.hashCode()).isNotEqualTo(other.hashCode());
            });
        }
    }

}
