package com.github.junpakpark.productmanage.member.exception;

import com.github.junpakpark.productmanage.common.error.ErrorCode;
import com.github.junpakpark.productmanage.common.error.exception.ConflictException;

public class EmailConflictException extends ConflictException {

    public EmailConflictException(final MemberErrorCode errorCode) {
        super(new ErrorCode<>(errorCode.getCode(), errorCode.getMessage()));
    }

}
