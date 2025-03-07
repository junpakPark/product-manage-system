package com.github.junpakpark.productmanage.product.command.application.port.in;

import com.github.junpakpark.productmanage.common.resolver.memberinfo.MemberInfo;
import com.github.junpakpark.productmanage.product.command.application.port.in.web.ProductCommand;

public interface ProductUseCase {

    Long create(final MemberInfo memberInfo, final ProductCommand command);

    void update(final MemberInfo memberInfo, final Long productId, final ProductCommand command);

    void delete(final MemberInfo memberInfo, final Long productId);

}
