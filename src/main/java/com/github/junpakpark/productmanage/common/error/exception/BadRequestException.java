package com.github.junpakpark.productmanage.common.error.exception;

import com.github.junpakpark.productmanage.common.error.ErrorCode;
import org.springframework.http.HttpStatus;

public class BadRequestException extends GlobalException {

    public BadRequestException(ErrorCode<?> errorCode) {
        super(errorCode, HttpStatus.BAD_REQUEST);
    }

}
