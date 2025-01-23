package com.elice.team04backend.common.dto.request;

import com.elice.team04backend.common.utils.Regex;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "회원가입 요청 데이터")
public class SignUpRequestDto {

    @NotBlank(message = "이메일은 필수 입력 값입니다")
    @Email(message = "이메일 형식으로 되어있어야 합니다")
    @Size(max = 300, message = "이메일은 최대 300글자 입니다")
    @Schema(description = "사용자 이메일", example = "example@domain.com")
    private String email;

    @Schema(description = "사용자 이름", example = "홍길동")
    private String username;

    @NotBlank(message = "비밀번호는 필수 입력 값입니다")
    @Pattern(regexp = Regex.PASSWORD, message = "비밀번호는 8 ~ 16자, 영문 대소문자, 숫자, @ 및 ! 특수문자를 포함해야 합니다.")
    @Schema(description = "사용자 비밀번호", example = "!a12345678")
    private String password;
}