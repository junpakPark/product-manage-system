package com.github.junpakpark.productmanage.product.query.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.github.junpakpark.productmanage.common.config.QueryDslConfig;
import com.github.junpakpark.productmanage.product.command.domain.Money;
import com.github.junpakpark.productmanage.product.command.domain.Name;
import com.github.junpakpark.productmanage.product.command.domain.Product;
import com.github.junpakpark.productmanage.product.command.domain.option.InputOption;
import com.github.junpakpark.productmanage.product.command.domain.option.OptionChoice;
import com.github.junpakpark.productmanage.product.command.domain.option.ProductOption;
import com.github.junpakpark.productmanage.product.command.domain.option.SelectOption;
import com.github.junpakpark.productmanage.product.exception.ProductErrorCode;
import com.github.junpakpark.productmanage.product.exception.ProductNotFoundException;
import com.github.junpakpark.productmanage.product.query.dto.ProductDetailResponse;
import com.github.junpakpark.productmanage.product.query.dto.ProductOptionResponse;
import com.github.junpakpark.productmanage.product.query.dto.ProductSummaryResponse;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.List;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@DataJpaTest
@Import({QueryDslConfig.class, ProductQueryRepository.class})
class ProductQueryRepositoryTest {

    @Autowired
    private ProductQueryRepository productQueryRepository;
    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    void setup() {
        persistTestData();
        entityManager.flush();
        entityManager.clear();
    }

    @Test
    @DisplayName("상품 목록 조회")
    void findAllProducts() {
        //Arrange
        final Pageable pageable = PageRequest.of(0, 10);

        // Action
        final Page<ProductSummaryResponse> result = productQueryRepository.findAllProducts(pageable);

        // Assert
        SoftAssertions.assertSoftly(softly -> {
            assertThat(result.getTotalElements()).isEqualTo(2);
            assertThat(result.getContent()).extracting("name")
                    .containsExactly("상품2", "상품1");
        });
    }

    @Test
    @DisplayName("상품 단건 조회")
    void getProductDetail() {
        // Arrange
        final Product product = findProduct1();

        // Action
        final ProductDetailResponse detail = productQueryRepository.getProductDetail(product.getId());

        // Assert
        SoftAssertions.assertSoftly(softly -> {
            assertThat(detail.name()).isEqualTo("상품1");
            assertThat(detail.options()).hasSize(2);
        });
    }

    @Test
    @DisplayName("상품 옵션 조회")
    void getProductOptions() {
        // Arrange
        final Product product = findProduct1();

        // Action
        final List<ProductOptionResponse> result = productQueryRepository.getProductOptions(product.getId());

        // Assert
        final ProductOptionResponse colorOption = result.stream()
                .filter(o -> o.name().equals("색상"))
                .findFirst()
                .orElseThrow();

        final ProductOptionResponse engravingOption = result.stream()
                .filter(o -> o.name().equals("각인문구"))
                .findFirst()
                .orElseThrow();

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(result).hasSize(2);
            softly.assertThat(colorOption.choices()).containsExactly("빨강", "파랑");
            softly.assertThat(engravingOption.choices()).isEmpty();
        });
    }

    @Test
    @DisplayName("존재하지 않는 상품의 옵션 조회 시 예외 발생")
    void getProductOptions_NotFound() {
        // Arrange
        final Long invalidProductId = 999L;

        // Action
        // Assert
        assertThatThrownBy(() -> productQueryRepository.getProductOptions(invalidProductId))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessage(ProductErrorCode.PRODUCT_NOT_FOUND.getMessage() + " 상품 ID: 999");
    }


    @Test
    @DisplayName("삭제된_상품_조회되지_않음")
    void softDelete() {
        // Arrange
        final Product product = findProduct1();
        entityManager.remove(product);

        entityManager.flush();
        entityManager.clear();

        final Pageable pageable = PageRequest.of(0, 10);

        //Action
        final Page<ProductSummaryResponse> result = productQueryRepository.findAllProducts(pageable);

        // Assert
        SoftAssertions.assertSoftly(softly -> {
            assertThat(result.getTotalElements()).isEqualTo(1);
            assertThat(result.getContent()).extracting("name")
                    .containsExactly("상품2");
        });
    }

    private Product findProduct1() {
        return entityManager.createQuery("SELECT p FROM Product p WHERE p.name.value = :name", Product.class)
                .setParameter("name", "상품1")
                .getSingleResult();
    }

    private void persistTestData() {
        final Product product1 = new Product(
                new Name("상품1"),
                "설명1",
                new Money(BigDecimal.valueOf(10000)),
                new Money(BigDecimal.valueOf(2500)),
                1L
        );
        final Product product2 = new Product(
                new Name("상품2"),
                "설명2",
                new Money(BigDecimal.valueOf(15000)),
                new Money(BigDecimal.valueOf(3000)),
                2L
        );

        final ProductOption option1 = new SelectOption(
                new Name("색상"),
                new Money(BigDecimal.ZERO),
                List.of(new OptionChoice("빨강"), new OptionChoice("파랑"))
        );

        final ProductOption option2 = new InputOption(
                new Name("각인문구"),
                new Money(BigDecimal.valueOf(2000))
        );

        product1.addOption(option1);
        product1.addOption(option2);

        entityManager.persist(product1);
        entityManager.persist(product2);
    }

}
