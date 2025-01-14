package com.elice.team04backend.common.exception;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException{
    private ErrorCode errorCode;

    public CustomException(ErrorCode errorCode){
        super(errorCode.message);
        this.errorCode = errorCode;
    }

    public ExceptionResponse toResponse(){
        return new ExceptionResponse(errorCode);
    }

}
