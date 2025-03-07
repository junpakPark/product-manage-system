package com.github.junpakpark.productmanage.common.error.exception;

import com.github.junpakpark.productmanage.common.domain.Role;
import com.github.junpakpark.productmanage.common.error.ErrorCode;
import org.springframework.http.HttpStatus;

public class ForbiddenException extends GlobalException {

    public ForbiddenException(ErrorCode<?> errorCode) {
        super(errorCode, HttpStatus.FORBIDDEN);
    }

    public static class RoleForbiddenException extends ForbiddenException {
        public RoleForbiddenException(final Role role) {
            super(new ErrorCode<>("c-001", "%s 이상의 권한이 필요합니다.".formatted(role.getDescription())));
        }
    }

}
