package com.github.junpakpark.productmanage.product.command.domain;

import com.github.junpakpark.productmanage.product.exception.ProductErrorCode;
import com.github.junpakpark.productmanage.product.exception.ProductNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

    default Product getProductById(final Long productId) {
        return findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(ProductErrorCode.PRODUCT_NOT_FOUND, productId));
    }
}
