package com.github.junpakpark.productmanage.common.error.exception;

import com.github.junpakpark.productmanage.common.error.ErrorCode;
import org.springframework.http.HttpStatus;

public class UnauthorizedException extends GlobalException {

    public UnauthorizedException(ErrorCode<?> errorCode) {
        super(errorCode, HttpStatus.UNAUTHORIZED);
    }

}
