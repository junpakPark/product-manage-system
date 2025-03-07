package com.github.junpakpark.productmanage.product.command.application.port.in.web;

import com.github.junpakpark.productmanage.common.resolver.memberinfo.MemberInfo;

public interface ProductUseCase {

    Long create(final MemberInfo memberInfo, final ProductCommand command);

    void update(final MemberInfo memberInfo, final Long productId, final ProductCommand command);

    void delete(final MemberInfo memberInfo, final Long productId);

}
