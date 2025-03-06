package com.github.junpakpark.productmanage.product.command.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.github.junpakpark.productmanage.common.domain.Role;
import com.github.junpakpark.productmanage.common.resolver.memberinfo.MemberInfo;
import com.github.junpakpark.productmanage.product.command.ProductSteps;
import com.github.junpakpark.productmanage.product.command.application.port.in.web.CreateProductCommand;
import com.github.junpakpark.productmanage.product.command.application.port.in.web.UpdateProductCommand;
import com.github.junpakpark.productmanage.product.command.domain.Product;
import com.github.junpakpark.productmanage.product.command.domain.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
class ProductServiceTest {

    private ProductService sut;
    private ProductRepository productRepository;
    private CreateProductCommand createProductCommand;
    private MemberInfo memberInfo;

    @BeforeEach
    void setUp() {
        productRepository = new FakeProductRepository();
        sut = new ProductService(productRepository);
        memberInfo = new MemberInfo(1L, Role.SELLER);
        createProductCommand = ProductSteps.상품생성요청_생성();
    }

    @Test
    @DisplayName("정상적인 값으로 상품을 생성할 수 있다.")
    void create() {
        // Action
        final Long productId = sut.create(memberInfo, createProductCommand);

        // Assert
        assertThat(productId).isNotNull();
    }

    @Test
    @DisplayName("정상적으로 상품 정보를 수정할 수 있다.")
    void update() {
        // Arrange
        final Long productId = sut.create(memberInfo, createProductCommand);
        final UpdateProductCommand command = ProductSteps.상품수정요청_생성();

        // Action
        sut.update(memberInfo, productId, command);

        // Assert
        final Product updatedProduct = productRepository.getProductById(productId);
        assertThat(updatedProduct.getProductInfo().getDescription()).isEqualTo("새로운 상품 설명");
    }

    @Test
    @DisplayName("정상적으로 상품을 삭제할 수 있다.")
    void delete() {
        // Arrange
        final Long productId = sut.create(memberInfo, createProductCommand);

        // Action
        sut.delete(memberInfo, productId);

        // Assert
        assertThatThrownBy(() -> productRepository.getProductById(productId))
                .isInstanceOf(IllegalArgumentException.class);
    }

}
