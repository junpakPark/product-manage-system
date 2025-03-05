package com.github.junpakpark.productmanage.member.application.port.in.web;

import com.github.junpakpark.productmanage.common.security.application.dto.MemberInfo;

public interface ValidateMemberUseCase {

    MemberInfo validateMember(final LoginCommand command);
}
