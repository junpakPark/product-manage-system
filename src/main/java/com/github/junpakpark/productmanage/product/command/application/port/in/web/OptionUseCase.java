package com.github.junpakpark.productmanage.product.command.application.port.in.web;

import com.github.junpakpark.productmanage.common.resolver.memberinfo.MemberInfo;

public interface OptionUseCase {

    void addOption(final MemberInfo memberInfo, final Long productId, final OptionCommand command);

    void updateOption(final MemberInfo memberInfo, final Long productId, final Long optionId, final OptionCommand command);

    void deleteOption(final MemberInfo memberInfo, final Long productId, final Long optionId);

}
