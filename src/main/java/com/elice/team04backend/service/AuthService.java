package com.elice.team04backend.service;

import com.elice.team04backend.common.dto.request.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

public interface AuthService {

    void verifyEmail(VerifyEmailRequestDto verifyEmailRequestDto);
    void confirmVerificationCode(ConfirmEmailRequestDto confirmEmailRequestDto);

    void signUp(SignUpRequestDto signUpRequestDto, MultipartFile profileImage);
    void logout(Long userId, HttpServletRequest request, HttpServletResponse response);

    String refreshAccessToken(String refreshToken);

    void resetPassword(ResetPasswordRequestDto resetPasswordRequestDto);
    void changePassword(Long userId, ChangePasswordRequestDto changePasswordRequestDto);

    void deactivateAccount(Long userId, HttpServletRequest request, HttpServletResponse response);
}
