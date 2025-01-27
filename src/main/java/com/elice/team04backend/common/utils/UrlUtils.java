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
    };
}
