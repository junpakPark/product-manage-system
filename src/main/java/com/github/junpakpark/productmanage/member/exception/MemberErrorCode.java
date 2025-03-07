package com.github.junpakpark.productmanage.member.exception;

import lombok.Getter;

@Getter
public enum MemberErrorCode {

    EMAIL_CONFLICT("MEMBER-001","이미 존재하는 회원의 이메일입니다."),
    PASSWORD_UNMATCHED("MEMBER-002", "비밀번호가 일치하지 않습니다."),
    MEMBER_NOT_FOUND("MEMBER-003","존재하지 않는 회원입니다.")
    ;

    private final String code;
    private final String message;

    MemberErrorCode(final String code, final String message) {
        this.code = code;
        this.message = message;
    }

}
