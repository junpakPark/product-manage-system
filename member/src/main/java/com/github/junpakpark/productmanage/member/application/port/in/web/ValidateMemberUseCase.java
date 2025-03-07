package com.github.junpakpark.productmanage.member.application.port.in.web;

import com.github.junpakpark.productmanage.common.resolver.memberinfo.MemberInfo;

public interface ValidateMemberUseCase {

    MemberInfo validateMember(final LoginCommand command);
}
