package com.elice.team04backend.common.utils;

public class UrlUtils {
    public static final String[] PermittedUrl = {
            "/api/auth/verify-email",
            "/api/auth/verify",
            "/api/auth/reset-password",
            "/api/auth/signup",
            "/api/auth/login",
            "/api/auth/refresh-token",
            "/api/auth/kakao/login/**",
            "/api/auth/google/login/**",
            "/api/auth/naver/login/**",
            "/api/auth/validate-email",
            "/swagger-ui/**",
            "/api-docs/**",
            "/api/accept/**",
            "/*.html",          // 루트 경로의 모든 HTML 파일
            "/chat.html",        // 채팅 페이지 명시적 허용
            "/favicon.ico",
            "/api/messages/get/**",  // 추가한 주소 (모든 메시지 관련 API 허용)
            "/chat/**"            // 추가한 주소 (채팅 관련 WebSocket 허용)
    };
}
