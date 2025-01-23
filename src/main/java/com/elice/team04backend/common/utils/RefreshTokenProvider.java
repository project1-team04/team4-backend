package com.elice.team04backend.common.utils;

import com.elice.team04backend.common.config.property.TokenProperty;
import com.elice.team04backend.entity.User;
import com.elice.team04backend.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
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

    public void setRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        int cookieMaxAge = (int) (getRefreshTokenExpiration() * 60 * 60); // 초 단위로 변환

        // 쿠키 생성
        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
        refreshTokenCookie.setHttpOnly(true); // JavaScript에서 접근 불가
        refreshTokenCookie.setPath("/");     // 애플리케이션 전체에서 사용 가능
        refreshTokenCookie.setMaxAge(cookieMaxAge); // 쿠키 유효 기간 설정

        // 쿠키 추가
        response.addCookie(refreshTokenCookie);
    }
}
