package com.elice.team04backend.common.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "회원가입 요청 데이터")
public class SignUpRequestDto {

    @Schema(description = "사용자 이메일", example = "example@domain.com")
    private String email;

    @Schema(description = "사용자 이름", example = "홍길동")
    private String username;

    @Schema(description = "사용자 비밀번호", example = "password123")
    private String password;
}