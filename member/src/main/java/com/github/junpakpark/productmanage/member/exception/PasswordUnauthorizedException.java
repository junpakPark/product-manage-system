package com.github.junpakpark.productmanage.member.exception;

import com.github.junpakpark.productmanage.common.error.ErrorCode;
import com.github.junpakpark.productmanage.common.error.exception.UnauthorizedException;

public class PasswordUnauthorizedException extends UnauthorizedException {

    public PasswordUnauthorizedException(final MemberErrorCode errorCode) {
        super(new ErrorCode<>(errorCode.getCode(), errorCode.getMessage()));
    }

}
