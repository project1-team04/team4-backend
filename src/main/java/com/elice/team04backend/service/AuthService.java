package com.elice.team04backend.service;

import com.elice.team04backend.common.dto.request.SignUpRequestDto;
import com.elice.team04backend.common.dto.request.ConfirmEmailRequestDto;
import com.elice.team04backend.common.dto.request.VerifyEmailRequestDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {

    void verifyEmail(VerifyEmailRequestDto verifyEmailRequestDto);
    void confirmVerificationCode(ConfirmEmailRequestDto confirmEmailRequestDto);

    void signUp(SignUpRequestDto signUpRequestDto);
    void logout(Long userId, HttpServletRequest request, HttpServletResponse response);

    String refreshAccessToken(String refreshToken);
}
