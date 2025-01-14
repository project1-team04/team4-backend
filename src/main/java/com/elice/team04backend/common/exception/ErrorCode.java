package com.elice.team04backend.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    //Common Errors
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "INVALID_INPUT_VALUE", "잘못된 입력 값입니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "METHOD_NOT_ALLOWED", "지원하지 않는 HTTP 메서드입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", "서버에서 오류가 발생했습니다."),

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "유저를 찾을 수 없습니다."),
    USER_CREATE_FAILED(HttpStatus.BAD_REQUEST, "USER_CREATE_FAILED", "유저 생성에 실패했습니다."),

    //Project Errors
    PROJECT_NOT_FOUND(HttpStatus.NOT_FOUND, "PROJECT_NOT_FOUND", "프로젝트를 찾을 수 없습니다."),
    PROJECT_CREATE_FAILED(HttpStatus.BAD_REQUEST, "PROJECT_CREATE_FAILED", "프로젝트 생성에 실패했습니다."),

    //Issue Errors
    ISSUE_NOT_FOUND(HttpStatus.NOT_FOUND, "ISSUE_NOT_FOUND", "이슈를 찾을 수 없습니다."),
    ISSUE_CREATE_FAILED(HttpStatus.BAD_REQUEST, "ISSUE_CREATION_FAILED", "이슈 생성에 실패했습니다."),

    //Label Errors
    LABEL_NOT_FOUND(HttpStatus.NOT_FOUND, "LABEL_NOT_FOUND", "라벨을 찾을 수 없습니다."),
    LABEL_CREATE_FAILED(HttpStatus.BAD_REQUEST, "LABEL_CREATE_FAILED", "라벨 생성에 실패했습니다.");
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
