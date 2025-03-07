package com.github.junpakpark.productmanage.common.error.exception;

import com.github.junpakpark.productmanage.common.error.ErrorCode;
import org.springframework.http.HttpStatus;

public class ForbiddenException extends GlobalException {

    public ForbiddenException(ErrorCode<?> errorCode) {
        super(errorCode, HttpStatus.FORBIDDEN);
    }

}
