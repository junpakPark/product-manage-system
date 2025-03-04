package com.github.junpakpark.productmanage.common.security.application.dto;

import com.github.junpakpark.productmanage.member.domain.Role;

public record MemberInfo(
        Long memberId,
        Role role
) {
}
