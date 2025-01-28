package com.elice.team04backend.common.exception;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
    private final ErrorCode errorCode;

    public CustomException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ExceptionResponse toResponse() {
        return new ExceptionResponse(errorCode);
    }
}
