package com.elice.team04backend.common.exception;


import lombok.Getter;

@Getter
public class ExceptionResponse {

    private int code;
    private String codeName;
    private String message;

    public ExceptionResponse(ErrorCode errorCode){
        code = errorCode.code;
        codeName = errorCode.codeName;
        message = errorCode.message;
    }
}
