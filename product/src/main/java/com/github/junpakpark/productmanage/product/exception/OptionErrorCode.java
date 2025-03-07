package com.github.junpakpark.productmanage.product.exception;

import lombok.Getter;

@Getter
public enum OptionErrorCode {
    NAME_NULL_BAD_REQUEST("OPTION-001", "옵션명은 필수입니다."),
    ADDITIONAL_PRICE_NULL_BAD_REQUEST("OPTION-002", "추가 요금은 필수입니다."),
    SIZE_BAD_REQUEST("OPTION-003", "옵션은 %d개를 초과할 수 없습니다."),
    ASSOCIATION_CONFLICT("OPTION-004", "이미 다른 상품에 연결된 옵션입니다."),
    TYPE_BAD_REQUEST("OPTION-005", "옵션 타입은 변경할 수 없습니다."),
    NAME_DUPLICATE_BAD_REQUEST("OPTION-006", "동일한 이름의 옵션이 이미 존재합니다."),
    INPUT_OPTION_CONFLICT("OPTION-007", "해당 옵션은 선택지를 가질 수 없습니다."),
    SELECT_CHOICES_NULL_BAD_REQUEST("OPTION-008", "옵션 선택지는 필수입니다."),
    SELECT_CHOICES_EMPTY_BAD_REQUEST("OPTION-009", "선택지는 적어도 하나는 있어야 합니다."),
    CHOICE_BLANK_BAD_REQUEST("OPTION-010", "선택지는 비어있을 수 없습니다."),
    CHOICE_LENGTH_BAD_REQUEST("OPTION-011", "선택지는 최대 %d자 이하여야 합니다."),
    ;

    private final String code;
    private final String message;

    OptionErrorCode(final String code, final String message) {
        this.code = code;
        this.message = message;
    }
}
