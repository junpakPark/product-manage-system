package com.github.junpakpark.productmanage.product.command.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.github.junpakpark.productmanage.common.domain.Role;
import com.github.junpakpark.productmanage.common.error.exception.ForbiddenException.RoleForbiddenException;
import com.github.junpakpark.productmanage.common.resolver.memberinfo.MemberInfo;
import com.github.junpakpark.productmanage.product.command.application.port.in.web.OptionCommand;
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

class OptionServiceTest {

    private OptionService sut;
    private ProductRepository productRepository;
    private MemberInfo memberInfo;
    private Long productId;
    private OptionCommand inputCommand;
    private OptionCommand selectCommand;

    @BeforeEach
    void setUp() {
        productRepository = new FakeProductRepository();
        sut = new OptionService(productRepository, new OptionFactory());
        memberInfo = new MemberInfo(1L, Role.SELLER);
        productId = productRepository.save(new Product(
                new Name("상품명"),
                "상품 설명",
                new Money(BigDecimal.valueOf(1000L)),
                new Money(BigDecimal.valueOf(100L)),
                1L
        )).getId();
        inputCommand = new OptionCommand(
                "원하시는 문구를 입력해주세요",
                OptionType.INPUT,
                BigDecimal.valueOf(1000),
                Collections.emptyList()
        );
        selectCommand = new OptionCommand(
                "원하시는 색상을 선택해주세요",
                OptionType.SELECT,
                BigDecimal.valueOf(1000),
                List.of("빨강", "파랑")
        );
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
