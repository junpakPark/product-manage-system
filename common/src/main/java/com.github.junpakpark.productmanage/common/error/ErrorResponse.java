package com.github.junpakpark.productmanage.common.error;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

public record ErrorResponse(
        String errorCode,
        String errorMessage
) {

    public static ErrorResponse from(final ErrorCode<?> errorCode) {
        return new ErrorResponse(errorCode.getCode(), errorCode.getMessage());
    }

    public static ErrorResponse of(final String errorCode, final BindingResult bindingResult) {
        return new ErrorResponse(errorCode, formatErrors(bindingResult));
    }

    private static String formatErrors(BindingResult bindingResult) {
        final List<FieldError> fieldErrors = bindingResult.getFieldErrors();

        if (fieldErrors.isEmpty()) {
            return "No validation errors";
        }
        return fieldErrors.stream()
                .map(ErrorResponse::formatFieldError)
                .collect(Collectors.joining(", "));
    }

    private static String formatFieldError(FieldError error) {
        return String.format("[%s]: %s", error.getField(), error.getDefaultMessage());
    }
}
