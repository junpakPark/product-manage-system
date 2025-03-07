package com.github.junpakpark.productmanage.product.command.application;

import com.github.junpakpark.productmanage.common.resolver.memberinfo.MemberInfo;
import com.github.junpakpark.productmanage.product.command.application.port.in.web.OptionCommand;
import com.github.junpakpark.productmanage.product.command.application.port.in.web.OptionUseCase;
import com.github.junpakpark.productmanage.product.command.domain.Product;
import com.github.junpakpark.productmanage.product.command.domain.ProductRepository;
import com.github.junpakpark.productmanage.product.command.domain.option.OptionFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class OptionService implements OptionUseCase {

    private final ProductRepository productRepository;
    private final OptionFactory optionFactory;

    @Override
    public void addOption(final MemberInfo memberInfo, final Long productId, final OptionCommand command) {
        memberInfo.role().validateSeller();

        final Product product = productRepository.getProductById(productId);
        product.validateOwner(memberInfo.memberId());

        product.addOption(optionFactory.createOption(command));
    }

    @Override
    public void updateOption(
            final MemberInfo memberInfo,
            final Long productId,
            final Long optionId,
            final OptionCommand command
    ) {
        memberInfo.role().validateSeller();

        final Product product = productRepository.getProductById(productId);
        product.validateOwner(memberInfo.memberId());

        product.updateOption(optionId, optionFactory.createOption(command));
    }

    @Override
    public void deleteOption(final MemberInfo memberInfo, final Long productId, final Long optionId) {
        memberInfo.role().validateSeller();

        final Product product = productRepository.getProductById(productId);
        product.validateOwner(memberInfo.memberId());

        product.removeOption(optionId);
    }

}
