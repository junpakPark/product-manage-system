package com.github.junpakpark.productmanage.member.application.port.in;

import com.github.junpakpark.productmanage.member.domain.Member;
import com.github.junpakpark.productmanage.member.domain.Password;
import com.github.junpakpark.productmanage.member.domain.Role;

public record RegisterMemberCommand(
        String name,
        String email,
        String password,
        Role role
) {

    public Member toMember(final Password password) {
        return new Member(
                name,
                email,
                password,
                role
        );
    }

}
