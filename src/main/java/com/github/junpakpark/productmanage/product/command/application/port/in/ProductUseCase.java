package com.github.junpakpark.productmanage.product.command.application.port.in;

import com.github.junpakpark.productmanage.common.resolver.memberinfo.MemberInfo;
import com.github.junpakpark.productmanage.product.command.application.port.in.web.CreateProductCommand;
import com.github.junpakpark.productmanage.product.command.application.port.in.web.UpdateProductCommand;

public interface ProductUseCase {

    Long create(final MemberInfo memberInfo, final CreateProductCommand command);

    void update(final MemberInfo memberInfo, final Long productId, final UpdateProductCommand command);

    void delete(final MemberInfo memberInfo, final Long productId);

}
