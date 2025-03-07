package com.github.junpakpark.productmanage.common.error.exception;

import com.github.junpakpark.productmanage.common.error.ErrorCode;
import org.springframework.http.HttpStatus;

public class NotFoundException extends GlobalException {

    public NotFoundException(ErrorCode<?> errorCode) {
        super(errorCode, HttpStatus.NOT_FOUND);
    }

}
