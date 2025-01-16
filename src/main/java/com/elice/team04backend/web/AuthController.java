package com.elice.team04backend.web;

import com.elice.team04backend.common.dto.request.SignUpRequestDto;
import com.elice.team04backend.dto.ConfirmEmailRequestDto;
import com.elice.team04backend.dto.VerifyEmailRequestDto;
import com.elice.team04backend.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "로그인, 회원가입 관련 API 입니다.")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "이메일 인증 코드 발급 요청", description = "이메일 인증을 위한 인증 코드를 발급 요청합니다.")
    @PostMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestBody @Valid VerifyEmailRequestDto verifyEmailRequestDto) {
        authService.verifyEmail(verifyEmailRequestDto);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "이메일 인증 코드 확인 요청", description = "인증 코드가 올바른지 확인하는 요청입니다.")
    @PostMapping("/verify")
    public ResponseEntity<?> confirmVerificationCode(@RequestBody @Valid ConfirmEmailRequestDto confirmEmailRequestDto) {
        authService.confirmVerificationCode(confirmEmailRequestDto);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "이메일 일반 회원가입", description = "이메일 인증을 통한 회원가입입니다.")
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody @Valid SignUpRequestDto signUpRequestDto) {
        authService.signUp(signUpRequestDto);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "로그아웃", description = "refreshToken을 제거하고, accessToken을 블랙리스트로 관리합니다.")
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        authService.logout();
        return null;
    }
}
