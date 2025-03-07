package com.github.junpakpark.productmanage.product.query.repository;

import static com.github.junpakpark.productmanage.product.command.domain.QProduct.product;
import static com.github.junpakpark.productmanage.product.command.domain.option.QProductOption.productOption;

import com.github.junpakpark.productmanage.product.command.domain.Product;
import com.github.junpakpark.productmanage.product.exception.ProductErrorCode;
import com.github.junpakpark.productmanage.product.exception.ProductNotFoundException;
import com.github.junpakpark.productmanage.product.query.dto.ProductDetailResponse;
import com.github.junpakpark.productmanage.product.query.dto.ProductOptionResponse;
import com.github.junpakpark.productmanage.product.query.dto.ProductSummaryResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductQueryRepository {

    private final JPAQueryFactory queryFactory;

    public Page<ProductSummaryResponse> findAllProducts(final Pageable pageable) {
        final List<ProductSummaryResponse> results = queryFactory
                .select(Projections.constructor(ProductSummaryResponse.class,
                        product.id,
                        product.name.value,
                        product.description,
                        product.price.amount,
                        product.shippingFee.amount))
                .from(product)
                .orderBy(product.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        final long total = queryFactory
                .select(product.count())
                .from(product)
                .fetchOne();

        return new PageImpl<>(results, pageable, total);
    }

    public ProductDetailResponse getProductDetail(final Long productId) {
        final Product fetchedProduct = queryFactory
                .selectFrom(product)
                .leftJoin(product.productOptions.options, productOption).fetchJoin()
                .where(product.id.eq(productId))
                .fetchOne();

        validateProductExists(productId, fetchedProduct);
        return ProductDetailResponse.from(fetchedProduct);
    }

    public List<ProductOptionResponse> getProductOptions(Long productId) {
        final Product fetchedProduct = queryFactory
                .selectFrom(product)
                .leftJoin(product.productOptions.options, productOption).fetchJoin()
                .where(product.id.eq(productId))
                .fetchOne();

        validateProductExists(productId, fetchedProduct);
        return fetchedProduct.getProductOptions().stream()
                .map(option -> ProductOptionResponse.of(option, option.optionChoices()))
                .toList();
    }

    private void validateProductExists(final Long productId, final Product fetchedProduct) {
        if (Objects.isNull(fetchedProduct)) {
            throw new ProductNotFoundException(ProductErrorCode.PRODUCT_NOT_FOUND, productId);
        }
    }
}
