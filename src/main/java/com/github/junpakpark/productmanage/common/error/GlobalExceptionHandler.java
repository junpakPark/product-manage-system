package com.github.junpakpark.productmanage.common.error;

import com.github.junpakpark.productmanage.common.error.exception.ForbiddenException;
import com.github.junpakpark.productmanage.common.error.exception.GlobalException;
import com.github.junpakpark.productmanage.common.error.exception.UnauthorizedException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BindException.class)
    protected ResponseEntity<ErrorResponse> handleBindException(final BindException e) {
        log.error("handleBindException", e);
        final ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.BAD_REQUEST.toString(), e.getBindingResult());

        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    protected ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(
            final MethodArgumentTypeMismatchException e
    ) {
        log.error("handleMethodArgumentTypeMismatchException", e);
        final ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.toString(), e.getMessage());

        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    protected ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupportedException(
            final HttpRequestMethodNotSupportedException e
    ) {
        log.error("handleHttpRequestMethodNotSupportedException", e);
        final ErrorResponse errorResponse = new ErrorResponse(HttpStatus.METHOD_NOT_ALLOWED.toString(), e.getMessage());

        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(errorResponse);
    }

    @ExceptionHandler(UnauthorizedException.class)
    protected ResponseEntity<ErrorResponse> handleTokenUnauthorizedException(
            final UnauthorizedException e,
            final HttpServletRequest request
    ) {
        final ErrorCode<?> errorCode = e.getErrorCode();
        log.warn(
                "[SECURITY EVENT]: ({}) {} \n 요청 IP: {}, 요청 URL: {}, 요청 Method: {}",
                errorCode.getCode(), errorCode.getMessage(),
                request.getRemoteAddr(), request.getRequestURI(), request.getMethod()
        );

        return ResponseEntity.status(e.getStatus()).body(ErrorResponse.from(errorCode));
    }

    @ExceptionHandler(ForbiddenException.class)
    protected ResponseEntity<ErrorResponse> handleRoleForbiddenException(
            final ForbiddenException e,
            final HttpServletRequest request
    ) {
        final ErrorCode<?> errorCode = e.getErrorCode();
        log.warn(
                "[SECURITY EVENT]: ({}) {} \n 요청 IP: {}, 요청 URL: {}, 요청 Method: {}",
                errorCode.getCode(), errorCode.getMessage(),
                request.getRemoteAddr(), request.getRequestURI(), request.getMethod()
        );

        return ResponseEntity.status(e.getStatus()).body(ErrorResponse.from(errorCode));
    }

    @ExceptionHandler(GlobalException.class)
    protected ResponseEntity<ErrorResponse> handleGlobalException(final GlobalException e) {
        log.error("handleGlobalException", e);
        final ErrorResponse errorResponse = ErrorResponse.from(e.getErrorCode());

        return ResponseEntity.status(e.getStatus()).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorResponse> handleException(final Exception e) {
        log.error("handleException", e);
        final ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                e.getMessage()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

}
