package com.github.junpakpark.productmanage.product.command.domain;

import java.util.NoSuchElementException;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

    default Product getProductById(final Long productId) {
        return findById(productId).orElseThrow(NoSuchElementException::new);
    }
}
