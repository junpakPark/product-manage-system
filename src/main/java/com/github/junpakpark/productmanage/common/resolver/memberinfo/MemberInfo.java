package com.github.junpakpark.productmanage.common.resolver.memberinfo;

import com.github.junpakpark.productmanage.common.domain.Role;

public record MemberInfo(
        Long memberId,
        Role role
) {
}
