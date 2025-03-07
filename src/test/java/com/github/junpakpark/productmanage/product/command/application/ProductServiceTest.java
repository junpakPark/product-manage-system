package com.github.junpakpark.productmanage.product.command.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.github.junpakpark.productmanage.common.domain.Role;
import com.github.junpakpark.productmanage.common.error.exception.ForbiddenException.RoleForbiddenException;
import com.github.junpakpark.productmanage.common.resolver.memberinfo.MemberInfo;
import com.github.junpakpark.productmanage.product.command.OptionSteps;
import com.github.junpakpark.productmanage.product.command.ProductSteps;
import com.github.junpakpark.productmanage.product.command.application.port.in.web.OptionCommand;
import com.github.junpakpark.productmanage.product.command.application.port.in.web.ProductCommand;
import com.github.junpakpark.productmanage.product.command.domain.Money;
import com.github.junpakpark.productmanage.product.command.domain.Name;
import com.github.junpakpark.productmanage.product.command.domain.Product;
import com.github.junpakpark.productmanage.product.command.domain.ProductRepository;
import com.github.junpakpark.productmanage.product.command.domain.option.OptionChoice;
import com.github.junpakpark.productmanage.product.command.domain.option.OptionFactory;
import com.github.junpakpark.productmanage.product.command.domain.option.OptionType;
import com.github.junpakpark.productmanage.product.command.domain.option.ProductOption;
import com.github.junpakpark.productmanage.product.command.domain.option.SelectOption;
import com.github.junpakpark.productmanage.product.exception.OptionErrorCode;
import com.github.junpakpark.productmanage.product.exception.ProductBadRequestException;
import com.github.junpakpark.productmanage.product.exception.ProductErrorCode;
import com.github.junpakpark.productmanage.product.exception.ProductFobiddenException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

@SuppressWarnings("NonAsciiCharacters")
class ProductServiceTest {

    private ProductService sut;
    private ProductRepository productRepository;
    private ProductCommand productCommand;
    private MemberInfo memberInfo;
    private Long productId;

    @BeforeEach
    void setUp() {
        productRepository = new FakeProductRepository();
        sut = new ProductService(productRepository, new OptionFactory());
        memberInfo = new MemberInfo(1L, Role.SELLER);
        productCommand = ProductSteps.상품생성요청_생성();
        productId = sut.create(memberInfo, productCommand);
    }

    @Nested
    class CreateTest {

        @Test
        @DisplayName("정상적인 값으로 상품을 생성할 수 있다.")
        void success() {
            // Assert
            assertThat(productId).isNotNull();
        }

        @Test
        @DisplayName("판매자가 아니면 상품을 생성할 수 없다")
        void notSeller() {
            // Arrange
            final MemberInfo buyer = new MemberInfo(1L, Role.BUYER);

            // Action
            // Assert
            assertThatThrownBy(() -> sut.create(buyer, productCommand))
                    .isInstanceOf(RoleForbiddenException.class)
                    .hasMessage("판매자 이상의 권한이 필요합니다.");
        }

        @Test
        @DisplayName("상품명 누락 시 예외 발생")
        void nullName() {
            final ProductCommand invalidCommand = new ProductCommand(
                    null, "상품 설명", BigDecimal.valueOf(1000), BigDecimal.valueOf(100)
            );

            assertThatThrownBy(() -> sut.create(memberInfo, invalidCommand))
                    .isInstanceOf(ProductBadRequestException.class)
                    .hasMessage(ProductErrorCode.NAME_BAD_REQUEST.getMessage());
        }

        @Test
        @DisplayName("가격 누락 시 예외 발생")
        void nullPrice() {
            final ProductCommand invalidCommand = new ProductCommand(
                    "상품명", "상품 설명", null, BigDecimal.valueOf(100)
            );

            assertThatThrownBy(() -> sut.create(memberInfo, invalidCommand))
                    .isInstanceOf(ProductBadRequestException.class)
                    .hasMessage(ProductErrorCode.PRICE_BAD_REQUEST.getMessage());
        }

        @Test
        @DisplayName("배송비 누락 시 예외 발생")
        void nullShippingFee() {
            final ProductCommand invalidCommand = new ProductCommand(
                    "상품명", "상품 설명", BigDecimal.valueOf(100), null
            );

            assertThatThrownBy(() -> sut.create(memberInfo, invalidCommand))
                    .isInstanceOf(ProductBadRequestException.class)
                    .hasMessage(ProductErrorCode.PRICE_BAD_REQUEST.getMessage());
        }

    }

    @Nested
    class UpdateTest {

        private ProductCommand updateCommand;

        @BeforeEach
        void setUp() {
            updateCommand = ProductSteps.상품수정요청_생성();
        }

        @Test
        @DisplayName("정상적으로 상품 정보를 수정할 수 있다.")
        void success() {
            // Action
            sut.update(memberInfo, productId, updateCommand);

            // Assert
            final Product updatedProduct = productRepository.getProductById(productId);
            assertThat(updatedProduct.getDescription()).isEqualTo("새로운 상품 설명");
        }

        @Test
        @DisplayName("판매자가 아니면 상품을 수정할 수 없다")
        void notSeller() {
            // Arrange
            final MemberInfo buyer = new MemberInfo(1L, Role.BUYER);

            // Action
            // Assert
            assertThatThrownBy(() -> sut.update(buyer, productId, updateCommand))
                    .isInstanceOf(RoleForbiddenException.class)
                    .hasMessage("판매자 이상의 권한이 필요합니다.");
        }

        @Test
        @DisplayName("타 판매자가 상품을 수정할 수 없다")
        void differentSeller() {
            // Arrange
            final MemberInfo otherSeller = new MemberInfo(2L, Role.SELLER);

            // Action
            // Assert
            assertThatThrownBy(() -> sut.update(otherSeller, productId, updateCommand))
                    .isInstanceOf(ProductFobiddenException.class)
                    .hasMessageContaining(ProductErrorCode.PRODUCT_FORBIDDEN.getMessage() + " 회원 ID: 2");
        }

        @Test
        @DisplayName("존재하지 않는 상품 수정 시 예외 발생")
        void notFoundProduct() {
            // Arrange
            final Long invalidProductId = 999L;

            // Action
            // Assert
            assertThatThrownBy(() -> sut.update(memberInfo, invalidProductId, updateCommand))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("상품을 찾을 수 없습니다.");
        }

        @Test
        @DisplayName("상품명 누락 시 예외 발생")
        void missingName() {
            // Arrange
            final ProductCommand invalidCommand = new ProductCommand(
                    null, "새 설명", BigDecimal.valueOf(2000), BigDecimal.valueOf(200)
            );

            // Action
            // Assert
            assertThatThrownBy(() -> sut.update(memberInfo, productId, invalidCommand))
                    .isInstanceOf(ProductBadRequestException.class)
                    .hasMessage(ProductErrorCode.NAME_BAD_REQUEST.getMessage());
        }

    }

    @Nested
    class DeleteTest {

        @Test
        @DisplayName("정상적으로 상품을 삭제할 수 있다.")
        void success() {
            // Action
            sut.delete(memberInfo, productId);

            // Assert
            assertThatThrownBy(() -> productRepository.getProductById(productId))
                    .isInstanceOf(IllegalArgumentException.class);
        }


        @Test
        @DisplayName("판매자가 아니면 상품을 삭제할 수 없다")
        void notSeller() {
            // Arrange
            final MemberInfo buyer = new MemberInfo(1L, Role.BUYER);

            // Action
            // Assert
            assertThatThrownBy(() -> sut.delete(buyer, productId))
                    .isInstanceOf(RoleForbiddenException.class)
                    .hasMessage("판매자 이상의 권한이 필요합니다.");
        }

        @Test
        @DisplayName("타 판매자가 상품을 삭제할 수 없다")
        void differentSeller() {
            // Arrange
            final MemberInfo otherSeller = new MemberInfo(2L, Role.SELLER);

            // Action
            // Assert
            assertThatThrownBy(() -> sut.delete(otherSeller, productId))
                    .isInstanceOf(ProductFobiddenException.class)
                    .hasMessageContaining(ProductErrorCode.PRODUCT_FORBIDDEN.getMessage());
        }

        @Test
        @DisplayName("존재하지 않는 상품 삭제 시 예외 발생")
        void notFoundProduct() {
            // Arrange
            final Long invalidProductId = 999L;

            // Action
            // Assert
            assertThatThrownBy(() -> sut.delete(memberInfo, invalidProductId))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("상품을 찾을 수 없습니다.");
        }
    }


    @Nested
    class OptionTest {

        private OptionCommand inputCommand;
        private OptionCommand selectCommand;

        @BeforeEach
        void setUp() {
            inputCommand = OptionSteps.입력옵션생성요청_생성();
            selectCommand = OptionSteps.선택옵션생성요청_생성();
        }

        @Nested
        class AddTest {

            @Test
            @DisplayName("정상적으로 Input 옵션을 추가할 수 있다.")
            void input() {
                // Action
                sut.addOption(memberInfo, productId, inputCommand);

                // Assert
                final Product product = productRepository.getProductById(productId);
                assertThat(product.getProductOptions()).hasSize(1);
            }

            @Test
            @DisplayName("정상적으로 Select 옵션을 추가할 수 있다")
            void select() {
                // Action
                sut.addOption(memberInfo, productId, selectCommand);

                // Assert
                final Product product = productRepository.getProductById(productId);
                assertThat(product.getProductOptions()).hasSize(1);
            }

            @Test
            @DisplayName("판매자가 아니면 옵션을 추가할 수 없다")
            void notSeller() {
                // Arrange
                final MemberInfo buyer = new MemberInfo(1L, Role.BUYER);

                // Action
                // Assert
                assertThatThrownBy(() -> sut.addOption(buyer, productId, inputCommand))
                        .isInstanceOf(RoleForbiddenException.class)
                        .hasMessage("판매자 이상의 권한이 필요합니다.");
            }

            @Test
            @DisplayName("타 판매자가 옵션을 추가할 수 없다")
            void notOwner() {
                // Arrange
                final MemberInfo otherSeller = new MemberInfo(2L, Role.SELLER);

                // Action
                // Assert
                assertThatThrownBy(() -> sut.addOption(otherSeller, productId, inputCommand))
                        .isInstanceOf(ProductFobiddenException.class)
                        .hasMessageContaining(ProductErrorCode.PRODUCT_FORBIDDEN.getMessage() + " 회원 ID: 2");
            }

            @Test
            @DisplayName("존재하지 않는 상품에 옵션을 추가할 수 없다")
            void productNotFound() {
                // Arrange
                final Long invalidProductId = 999L;

                // Action
                // Assert
                assertThatThrownBy(() -> sut.addOption(memberInfo, invalidProductId, inputCommand))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining("상품을 찾을 수 없습니다.");
            }

            @Test
            @DisplayName("옵션이 3개를 초과하면 예외가 발생한다")
            void exceedMaxOptions() {
                // Arrange
                sut.addOption(memberInfo, productId, inputCommand);
                sut.addOption(memberInfo, productId, selectCommand);
                sut.addOption(
                        memberInfo,
                        productId,
                        new OptionCommand("옵션1", OptionType.INPUT, BigDecimal.valueOf(1000), Collections.emptyList())
                );
                final OptionCommand command = new OptionCommand("옵션4", OptionType.INPUT, BigDecimal.valueOf(1000),
                        Collections.emptyList());

                // Action
                // Assert
                assertThatThrownBy(() -> sut.addOption(memberInfo, productId, command))
                        .isInstanceOf(ProductBadRequestException.class)
                        .hasMessageContaining(OptionErrorCode.SIZE_BAD_REQUEST.getMessage().formatted(3));
            }

            @Test
            @DisplayName("동일한 이름의 옵션이 존재하면 예외가 발생한다")
            void duplicateOptionName() {
                // Arrange
                sut.addOption(memberInfo, productId, inputCommand);

                // Action
                // Assert
                assertThatThrownBy(() -> sut.addOption(memberInfo, productId, inputCommand))
                        .isInstanceOf(ProductBadRequestException.class)
                        .hasMessageContaining(OptionErrorCode.NAME_DUPLICATE_BAD_REQUEST.getMessage());
            }

            @Test
            @DisplayName("select 추가 시, 선택지가 없으면 예외가 발생한다")
            void selectFail() {
                // Arrange
                final OptionCommand command = new OptionCommand(
                        "원하시는 색상을 골라주세요",
                        OptionType.SELECT,
                        BigDecimal.valueOf(1000),
                        Collections.emptyList()
                );

                // Action
                // Assert
                assertThatThrownBy(() -> sut.addOption(memberInfo, productId, command))
                        .isInstanceOf(ProductBadRequestException.class)
                        .hasMessageContaining(OptionErrorCode.SELECT_CHOICES_EMPTY_BAD_REQUEST.getMessage());

            }

        }

        @Nested
        class UpdateTest {

            private Long optionId;

            @BeforeEach
            void setUp() {
                optionId = addOption();
            }

            @Test
            @DisplayName("정상적으로 옵션을 수정할 수 있다")
            void success() {
                // Action
                sut.updateOption(memberInfo, productId, optionId, selectCommand);

                // Assert
                final Product product = productRepository.getProductById(productId);
                final ProductOption option = product.getProductOptions().getFirst();
                assertThat(option.getName()).isEqualTo(new Name(selectCommand.name()));
            }

            @Test
            @DisplayName("타입에서 선택 타입으로 변화할 수 없다.")
            void typeUpdateFail() {
                // Action
                // Assert
                assertThatThrownBy(() -> sut.updateOption(memberInfo, productId, optionId, inputCommand))
                        .isInstanceOf(ProductBadRequestException.class)
                        .hasMessage(OptionErrorCode.TYPE_BAD_REQUEST.getMessage());
            }

            @Test
            @DisplayName("판매자가 아니면 옵션을 수정할 수 없다")
            void notSeller() {
                // Arrange
                final MemberInfo buyer = new MemberInfo(1L, Role.BUYER);

                // Action
                // Assert
                assertThatThrownBy(() -> sut.updateOption(buyer, productId, optionId, selectCommand))
                        .isInstanceOf(RoleForbiddenException.class)
                        .hasMessage("판매자 이상의 권한이 필요합니다.");
            }

            @Test
            @DisplayName("타 판매자는 옵션을 수정할 수 없다")
            void notOwner() {
                // Arrange
                final MemberInfo otherSeller = new MemberInfo(2L, Role.SELLER);

                // Action
                // Assert
                assertThatThrownBy(() -> sut.updateOption(otherSeller, productId, optionId, selectCommand))
                        .isInstanceOf(ProductFobiddenException.class)
                        .hasMessageContaining(ProductErrorCode.PRODUCT_FORBIDDEN.getMessage() + " 회원 ID: 2");
            }

            @Test
            @DisplayName("존재하지 않는 상품에 옵션을 수정할 수 없다")
            void productNotFound() {
                // Arrange
                final Long invalidProductId = 999L;

                // Action
                // Assert
                assertThatThrownBy(() -> sut.updateOption(memberInfo, invalidProductId, optionId, selectCommand))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining("상품을 찾을 수 없습니다.");
            }

            @Test
            @DisplayName("존재하지 않는 옵션을 수정할 수 없다")
            void optionNotFound() {
                // Arrange
                final Long invalidOptionId = 999L;

                // Action
                // Assert
                assertThatThrownBy(() -> sut.updateOption(memberInfo, productId, invalidOptionId, selectCommand))
                        .isInstanceOf(NoSuchElementException.class);
            }

            @Test
            @DisplayName("동일한 이름의 옵션이 이미 존재하는 경우 예외가 발생한다")
            void duplicateOptionName() {
                // Arrange
                sut.addOption(memberInfo, productId, inputCommand);
                final OptionCommand updateCommand = new OptionCommand(
                        "원하시는 문구를 입력해주세요",
                        OptionType.SELECT,
                        BigDecimal.valueOf(1000),
                        List.of("만수무강하세요", "생일축하합니다.")
                );

                // Action
                // Assert
                assertThatThrownBy(() -> sut.updateOption(memberInfo, productId, optionId, updateCommand))
                        .isInstanceOf(ProductBadRequestException.class)
                        .hasMessage(OptionErrorCode.NAME_DUPLICATE_BAD_REQUEST.getMessage());
            }

        }

        @Nested
        class DeleteTest {

            private Long optionId;

            @BeforeEach
            void setUp() {
                optionId = addOption();
            }

            @Test
            @DisplayName("정상적으로 옵션을 삭제할 수 있다")
            void success() {
                // Action
                sut.deleteOption(memberInfo, productId, optionId);

                // Assert
                final Product product = productRepository.getProductById(productId);
                assertThat(product.getProductOptions()).isEmpty();
            }

            @Test
            @DisplayName("판매자가 아니면 옵션을 삭제할 수 없다")
            void notSeller() {
                // Arrange
                final MemberInfo buyer = new MemberInfo(1L, Role.BUYER);

                // Action
                // Assert
                assertThatThrownBy(() -> sut.deleteOption(buyer, productId, optionId))
                        .isInstanceOf(RoleForbiddenException.class)
                        .hasMessage("판매자 이상의 권한이 필요합니다.");
            }

            @Test
            @DisplayName("타 판매자는 옵션을 삭제할 수 없다")
            void notOwner() {
                // Arrange
                final MemberInfo otherSeller = new MemberInfo(2L, Role.SELLER);

                // Action
                // Assert
                assertThatThrownBy(() -> sut.deleteOption(otherSeller, productId, optionId))
                        .isInstanceOf(ProductFobiddenException.class)
                        .hasMessage(ProductErrorCode.PRODUCT_FORBIDDEN.getMessage() + " 회원 ID: 2");
            }

            @Test
            @DisplayName("존재하지 않는 상품에 옵션을 삭제할 수 없다")
            void productNotFound() {
                // Arrange
                final Long invalidProductId = 999L;

                // Action
                // Assert
                assertThatThrownBy(() -> sut.deleteOption(memberInfo, invalidProductId, optionId))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining("상품을 찾을 수 없습니다.");
            }

            @Test
            @DisplayName("존재하지 않는 옵션을 삭제할 수 없다")
            void optionNotFound() {
                // Arrange
                final Long invalidOptionId = 999L;

                // Action
                // Assert
                assertThatThrownBy(() -> sut.deleteOption(memberInfo, productId, invalidOptionId))
                        .isInstanceOf(NoSuchElementException.class);
            }

        }

        private Long addOption() {
            final Product product = productRepository.getProductById(productId);
            final ProductOption option = new SelectOption(
                    new Name("원하시는 컬러을 선택해주세요"),
                    new Money(BigDecimal.valueOf(1000)),
                    List.of(new OptionChoice("레드"), new OptionChoice("블루"))
            );
            ReflectionTestUtils.setField(option, "id", 1L);
            product.addOption(option);

            return 1L;
        }

    }

}
