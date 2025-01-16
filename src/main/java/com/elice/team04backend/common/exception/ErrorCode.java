package com.elice.team04backend.common.exception;

import lombok.Getter;

@Getter
public class ErrorCode {

    // 다음과 같이 사용 -> USER_NOT_FOUND(400, "USER_NOT_FOUND", "사용자를 찾을 수 없음"),


    public final int code;

    public final String codeName;

    public final String message;

    ErrorCode(int code, String codeName, String message) {
        this.code = code;
        this.codeName = codeName;
        this.message = message;
    }
}
