package com.elice.team04backend.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "INVALID_INPUT_VALUE", "잘못된 입력 값입니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "METHOD_NOT_ALLOWED", "지원하지 않는 HTTP 메서드입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", "서버에서 오류가 발생했습니다.")

    ;

    private final HttpStatus httpStatus;
    private final String codeName;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String codeName, String message) {
        this.httpStatus = httpStatus;
        this.codeName = codeName;
        this.message = message;
    }
}
