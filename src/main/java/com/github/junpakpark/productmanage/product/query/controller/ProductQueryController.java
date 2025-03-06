package com.github.junpakpark.productmanage.product.query.controller;

import com.github.junpakpark.productmanage.product.query.dto.ProductDetailResponse;
import com.github.junpakpark.productmanage.product.query.dto.ProductOptionResponse;
import com.github.junpakpark.productmanage.product.query.dto.ProductSummaryResponse;
import com.github.junpakpark.productmanage.product.query.repository.ProductQueryRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductQueryController {

    private final ProductQueryRepository productQueryRepository;

    @GetMapping
    public Page<ProductSummaryResponse> getProducts(final Pageable pageable) {
        return productQueryRepository.findAllProducts(pageable);
    }

    @GetMapping("/{productId}")
    public ProductDetailResponse getProductDetail(@PathVariable Long productId) {
        return productQueryRepository.getProductDetail(productId);
    }

    @GetMapping("/{productId}/options")
    public List<ProductOptionResponse> getProductOptions(@PathVariable Long productId) {
        return productQueryRepository.getProductOptions(productId);
    }

}
