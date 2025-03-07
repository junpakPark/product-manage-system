package com.github.junpakpark.productmanage.product.command.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.github.junpakpark.productmanage.common.domain.Role;
import com.github.junpakpark.productmanage.common.error.exception.ForbiddenException.RoleForbiddenException;
import com.github.junpakpark.productmanage.common.resolver.memberinfo.MemberInfo;
import com.github.junpakpark.productmanage.product.command.application.port.in.web.ProductCommand;
import com.github.junpakpark.productmanage.product.command.domain.Product;
import com.github.junpakpark.productmanage.product.command.domain.ProductRepository;
import com.github.junpakpark.productmanage.product.exception.ProductBadRequestException;
import com.github.junpakpark.productmanage.product.exception.ProductErrorCode;
import com.github.junpakpark.productmanage.product.exception.ProductFobiddenException;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class ProductServiceTest {

    private ProductService sut;
    private ProductRepository productRepository;
    private ProductCommand productCommand;
    private MemberInfo memberInfo;
    private Long productId;

    @BeforeEach
    void setUp() {
        productRepository = new FakeProductRepository();
        sut = new ProductService(productRepository);
        memberInfo = new MemberInfo(1L, Role.SELLER);
        productCommand = new ProductCommand(
                "상품명",
                "상품 설명",
                BigDecimal.valueOf(1000L),
                BigDecimal.valueOf(100L)
        );
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
            updateCommand = new ProductCommand(
                    "새상품명",
                    "새로운 상품 설명",
                    BigDecimal.valueOf(2000L),
                    BigDecimal.valueOf(200L)
            );
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

}
