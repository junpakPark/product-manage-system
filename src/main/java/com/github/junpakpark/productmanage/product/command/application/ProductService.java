package com.github.junpakpark.productmanage.product.command.application;

import com.github.junpakpark.productmanage.common.resolver.memberinfo.MemberInfo;
import com.github.junpakpark.productmanage.product.command.application.port.in.ProductUseCase;
import com.github.junpakpark.productmanage.product.command.application.port.in.web.CreateProductCommand;
import com.github.junpakpark.productmanage.product.command.application.port.in.web.UpdateProductCommand;
import com.github.junpakpark.productmanage.product.command.domain.Product;
import com.github.junpakpark.productmanage.product.command.domain.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductService implements ProductUseCase {

    private final ProductRepository productRepository;

    @Override
    public Long create(final MemberInfo memberInfo, final CreateProductCommand command) {
        memberInfo.role().validateSeller();

        final Product product = command.toProduct(memberInfo.memberId());
        final Product savedProduct = productRepository.save(product);

        return savedProduct.getId();
    }

    @Override
    public void update(final MemberInfo memberInfo, final Long productId, final UpdateProductCommand command) {
        memberInfo.role().validateSeller();

        final Product product = productRepository.getProductById(productId);
        product.validateOwner(memberInfo.memberId());

        product.updateInfo(command.toProductInfo());
    }

    @Override
    public void delete(final MemberInfo memberInfo, final Long productId) {
        memberInfo.role().validateSeller();

        final Product product = productRepository.getProductById(productId);
        product.validateOwner(memberInfo.memberId());

        productRepository.delete(product);
    }

}
