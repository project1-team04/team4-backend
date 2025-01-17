package com.elice.team04backend.web;

import com.elice.team04backend.common.dto.request.SignUpRequestDto;
import com.elice.team04backend.common.model.UserDetailsImpl;
import com.elice.team04backend.common.dto.response.AccessTokenResponseDto;
import com.elice.team04backend.common.dto.request.ConfirmEmailRequestDto;
import com.elice.team04backend.common.dto.request.VerifyEmailRequestDto;
import com.elice.team04backend.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    @Operation(summary = "RefreshToken을 통한 AccessToken 발급 요청", description = "RefreshToken을 통한 AccessToken 발급 요청합니다.")
    @GetMapping("/refresh-token")
    public ResponseEntity<?> refreshAccessToken(HttpServletRequest request) {

        // Cookie에서 RefreshToken 추출
        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            return ResponseEntity.badRequest().build();
        }

        String refreshToken = null;
        for (Cookie cookie : cookies) {
            if ("refreshToken".equals(cookie.getName())) {
                refreshToken = cookie.getValue();
                break;
            }
        }

        if (refreshToken == null || refreshToken.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        // Access Token 발급
        String newAccessToken = authService.refreshAccessToken(refreshToken);

        return ResponseEntity.ok(new AccessTokenResponseDto(newAccessToken));
    }

    @Operation(summary = "로그아웃", description = "refreshToken을 제거하고, accessToken을 블랙리스트로 관리합니다.")
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@AuthenticationPrincipal UserDetailsImpl userDetails, HttpServletRequest request, HttpServletResponse response) {
        authService.logout(userDetails.getUserId(), request, response);
        return ResponseEntity.ok().build();
    }
}
