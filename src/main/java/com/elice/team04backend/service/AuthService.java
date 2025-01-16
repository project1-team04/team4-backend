package com.elice.team04backend.service;

import com.elice.team04backend.common.dto.request.SignUpRequestDto;
import com.elice.team04backend.dto.ConfirmEmailRequestDto;
import com.elice.team04backend.dto.VerifyEmailRequestDto;

public interface AuthService {

    void verifyEmail(VerifyEmailRequestDto verifyEmailRequestDto);
    void confirmVerificationCode(ConfirmEmailRequestDto confirmEmailRequestDto);

    void signUp(SignUpRequestDto signUpRequestDto);
    void logout();
}
