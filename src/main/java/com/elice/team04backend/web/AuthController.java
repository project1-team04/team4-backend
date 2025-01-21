package com.elice.team04backend.web;

import com.elice.team04backend.common.dto.request.*;
import com.elice.team04backend.common.model.UserDetailsImpl;
import com.elice.team04backend.common.dto.response.AccessTokenResponseDto;
import com.elice.team04backend.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @Operation(summary = "비밀번호 찾기(임시 비밀번호) 요청", description = "이메일을 입력하고 해당 이메일로 임시 비밀번호를 전송합니다.")
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody @Valid ResetPasswordRequestDto resetPasswordRequestDto) {
        authService.resetPassword(resetPasswordRequestDto);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "비밀번호 변경 요청", description = "이전 비밀번호를 검증하여 새로운 비밀번호로 변경합니다.")
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody @Valid ChangePasswordRequestDto changePasswordRequestDto) {
        authService.changePassword(userDetails.getUserId(), changePasswordRequestDto);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "이메일 일반 회원가입", description = "이메일 인증을 통한 회원가입입니다. 해당 API를 사용하려면, https://kdt-gitlab.elice.io/pttrack/class_01/web_project_i/team04/team04-documentations/-/issues/27 를 봐주세요")
    @PostMapping(value = "/signup", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> signup(
            @Parameter(description = "프로필 이미지 파일", content = @Content(mediaType = MediaType.IMAGE_PNG_VALUE))
            @RequestPart(value = "image", required = false) MultipartFile profileImage,
            @Parameter(description = "회원가입 요청 JSON", required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
            @RequestPart(value = "form") @Valid SignUpRequestDto signUpRequestDto) {
        authService.signUp(signUpRequestDto, profileImage);
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

    @Operation(summary = "회원 탈퇴", description = "서비스 회원 탈퇴를 진행합니다. (DB에서 실제 회원 정보가 삭제되지는 않습니다")
    @PostMapping("/deactivate")
    public ResponseEntity<?> deactivateAccount(@AuthenticationPrincipal UserDetailsImpl userDetails, HttpServletRequest request, HttpServletResponse response) {
        authService.deactivateAccount(userDetails.getUserId(), request, response);
        return ResponseEntity.ok().build();
    }
}
