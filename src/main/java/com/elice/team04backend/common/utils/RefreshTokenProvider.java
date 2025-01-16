package com.elice.team04backend.common.utils;

import com.elice.team04backend.common.config.property.TokenProperty;
import com.elice.team04backend.entity.User;
import com.elice.team04backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j(topic = "리프레시 토큰 생성 및 검증")
@Component
@RequiredArgsConstructor
public class RefreshTokenProvider {

    private final TokenProperty tokenProperty;
    private final UserRepository userRepository;

    @Transactional
    public String createAndStoreRefreshToken(Long userId) {
        String refreshToken = RefreshTokenGenerator.generateRefreshToken();
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        user.registerRefreshToken(refreshToken, LocalDateTime.now().plusHours(tokenProperty.getRefreshTokenExpiration()));

        return refreshToken;
    }

    // AccessToken 발급을 위한 User 조회를 동시에 진행.
    public User validateRefreshToken(String refreshToken) {
        User user = userRepository.findByRefreshToken(refreshToken).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));
        if(LocalDateTime.now().isBefore(user.getExpirationAt())) {
            return user;
        }
        return null;
    }

    public Long getRefreshTokenExpiration() {
        return tokenProperty.getRefreshTokenExpiration();
    }
}
