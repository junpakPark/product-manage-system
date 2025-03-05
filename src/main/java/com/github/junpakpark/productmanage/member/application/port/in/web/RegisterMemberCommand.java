package com.github.junpakpark.productmanage.member.application.port.in.web;

import com.github.junpakpark.productmanage.common.domain.Role;
import com.github.junpakpark.productmanage.member.domain.Member;
import com.github.junpakpark.productmanage.member.domain.Password;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegisterMemberCommand(
        @NotBlank(message = "이름은 필수입니다.")
        String name,
        @NotBlank(message = "이메일은 필수입니다.")
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        String email,
        @NotBlank(message = "비밀번호는 필수입니다.")
        @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
        String password,
        @NotNull(message = "회원 역할은 필수입니다.")
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
