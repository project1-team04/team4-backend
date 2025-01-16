package com.elice.team04backend.service.impl;

import com.elice.team04backend.common.constant.Provider;
import com.elice.team04backend.common.constant.UserStatus;
import com.elice.team04backend.common.dto.request.SignUpRequestDto;
import com.elice.team04backend.common.model.RedisDAO;
import com.elice.team04backend.common.service.EmailService;
import com.elice.team04backend.dto.ConfirmEmailRequestDto;
import com.elice.team04backend.dto.VerifyEmailRequestDto;
import com.elice.team04backend.entity.User;
import com.elice.team04backend.repository.UserRepository;
import com.elice.team04backend.service.AuthService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.elice.team04backend.common.utils.VerificationCodeGenerator.generateVerificationCode;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final RedisDAO redisDAO;


    @Override
    @Transactional
    public void signUp(SignUpRequestDto signUpRequestDto) {

        // 1. 이메일 중복 확인
        // 2. 이메일 인증 확인

        User signUpUser = User.builder()
                .email(signUpRequestDto.getEmail())
                .username(signUpRequestDto.getUsername())
                .password(passwordEncoder.encode(signUpRequestDto.getPassword()))
                .isVerified(true)
                .provider(Provider.EMAIL)
                .status(UserStatus.ACTIVE)
                .build();

        userRepository.save(signUpUser);
    }

    @Override
    public void logout() {

    }

    @Override
    public void confirmVerificationCode(ConfirmEmailRequestDto confirmEmailRequestDto) {
        String email = confirmEmailRequestDto.email();
        String savedVerificationCode = redisDAO.getValues(email);
        if(!savedVerificationCode.equals(confirmEmailRequestDto.verificationCode())){
            throw new IllegalStateException("잘못된 인증코드입니다.");
        }
        redisDAO.setValues(email, "VERIFIED", 300000L); // 5분간 해당 이메일이 인증됐음을 설정
    }

    @Override
    public void verifyEmail(VerifyEmailRequestDto verifyEmailRequestDto) {
        String verificationCode = generateVerificationCode();
        emailService.sendEmail(verifyEmailRequestDto.email(), "Threadly 인증코드 이메일", verificationCode);
        redisDAO.setValues(verifyEmailRequestDto.email(), verificationCode, 300000L); // 5분간 인증 설정
        log.info("{} 이메일 전송, 인증코드 {}", verifyEmailRequestDto.email(), verificationCode);
    }


}
