package com.elice.team04backend.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    // Common Errors
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "INVALID_INPUT_VALUE", "입력 값이 유효하지 않습니다. 필수값을 확인해주세요."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "METHOD_NOT_ALLOWED", "지원하지 않는 HTTP 메서드입니다. 요청 방식을 확인해주세요."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", "서버에서 알 수 없는 오류가 발생했습니다."),

    // Auth Errors
    INVALID_OAUTH_TYPE(HttpStatus.BAD_REQUEST, "INVALID_OAUTH_TYPE", "잘못된 소셜 로그인 타입입니다."),

    // Key Errors
    PROJECT_KEY_CREATE_FAILED(HttpStatus.CONFLICT, "PROJECT_KEY_CREATE_FAILED", "프로젝트 키 생성에 실패했습니다. 프로젝트는 영문으로 시작해야 합니다."),

    // User Errors
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "해당 유저를 찾을 수 없습니다."),
    USER_CREATE_FAILED(HttpStatus.BAD_REQUEST, "USER_CREATE_FAILED", "유저 생성 과정에서 오류가 발생했습니다."),
    USER_ALREADY_IN_PROJECT(HttpStatus.CONFLICT, "USER_ALREADY_IN_PROJECT","이미 프로젝트에 속해있는 유저입니다."),
    USER_ALREADY_INVITED(HttpStatus.CONFLICT, "USER_ALREADY_INVITED", "이미 프로젝트에 초대 된 유저입니다."),

    // Project Errors
    PROJECT_NOT_FOUND(HttpStatus.NOT_FOUND, "PROJECT_NOT_FOUND", "해당 프로젝트를 찾을 수 없습니다."),
    PROJECT_CREATE_FAILED(HttpStatus.BAD_REQUEST, "PROJECT_CREATE_FAILED", "프로젝트 생성을 실패했습니다. 다른 프로젝트명을 사용해주세요."),
    USER_NOT_IN_PROJECT(HttpStatus.FORBIDDEN, "USER_NOT_IN_PROJECT", "해당 유저는 프로젝트에 속해 있지 않습니다."),
    PROJECT_USERS_NOT_FOUND(HttpStatus.NOT_FOUND,"PROJECT_USERS_NOT_FOUND","해당 프로젝트에 연관된 유저가 없습니다."),

    // Issue Errors
    ISSUE_NOT_FOUND(HttpStatus.NOT_FOUND, "ISSUE_NOT_FOUND", "해당 이슈를 찾을 수 없습니다."),
    ISSUE_CREATE_FAILED(HttpStatus.BAD_REQUEST, "ISSUE_CREATE_FAILED", "이슈 생성 중 오류가 발생했습니다."),
    IMAGE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "IMAGE_UPLOAD_FAILED", "이미지 업로드 과정에서 오류가 발생했습니다."),
    IMAGE_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "IMAGE_DELETE_FAILED", "이미지 삭제 과정에서 오류가 발생했습니다."),

    // Label Errors
    LABEL_NOT_FOUND(HttpStatus.NOT_FOUND, "LABEL_NOT_FOUND", "해당 라벨을 찾을 수 없습니다."),
    LABEL_CREATE_FAILED(HttpStatus.BAD_REQUEST, "LABEL_CREATE_FAILED", "라벨 생성 중 오류가 발생했습니다."),

    // Role/Permission Errors
    ROLE_PERMISSION_DENIED(HttpStatus.FORBIDDEN, "ROLE_PERMISSION_DENIED", "해당 작업에 대한 권한이 없습니다."),
    ROLE_ACCESS_DENIED(HttpStatus.FORBIDDEN, "ROLE_ACCESS_DENIED", "해당 작업에 접근할 권한이 없습니다."),

    // Invitation Errors
    INVALID_INVITATION(HttpStatus.NOT_FOUND, "INVALID_INVITATION", "유효하지 않은 초대 토큰입니다."),
    LAST_MANAGER_CANNOT_LEAVE(HttpStatus.FORBIDDEN, "LAST_MANAGER_CANNOT_LEAVE", "마지막 관리자는 프로젝트를 탈퇴할 수 없습니다. 새로운 관리자를 지정해주세요."),
    NO_MEMBER_TO_ASSIGN(HttpStatus.BAD_REQUEST, "NO_MEMBER_TO_ASSIGN", "새 관리자를 지정할 수 있는 멤버가 없습니다."),
    EMAIL_SEND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "EMAIL_SEND_FAILED", "이메일 전송에 실패했습니다."),
    NEW_MANAGER_REQUIRED(HttpStatus.BAD_REQUEST, "NEW_MANAGER_REQUIRED", "마지막 관리자는 새로운 관리자를 지정해야 탈퇴할 수 있습니다."),
    NEW_MANAGER_NOT_FOUND(HttpStatus.NOT_FOUND, "NEW_MANAGER_NOT_FOUND", "지정된 새 관리자를 찾을 수 없습니다."),

    // Asynchronous Errors
    ASYNC_EXECUTION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "ASYNC_EXECUTION_FAILED", "비동기 작업 중 실행 오류 발생"),
    ASYNC_INTERRUPTED(HttpStatus.INTERNAL_SERVER_ERROR, "ASYNC_INTERRUPTED", "비동기 작업이 중단됨");

    private final HttpStatus httpStatus;
    private final String codeName;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String codeName, String message) {
        this.httpStatus = httpStatus;
        this.codeName = codeName;
        this.message = message;
    }
}
