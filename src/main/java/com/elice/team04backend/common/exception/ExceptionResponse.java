package com.elice.team04backend.common.exception;


import lombok.Getter;

@Getter
public class ExceptionResponse {
    private final int status;
    private final String codeName;
    private final String message;

    public ExceptionResponse(ErrorCode errorCode) {
        this.status = errorCode.getHttpStatus().value();
        this.codeName = errorCode.getCodeName();
        this.message = errorCode.getMessage();
    }
}
