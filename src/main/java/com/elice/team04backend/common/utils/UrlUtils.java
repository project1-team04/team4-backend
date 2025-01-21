package com.elice.team04backend.common.utils;

public class UrlUtils {
    public static final String[] PermittedUrl = {
            "/api/auth/verify-email",
            "/api/auth/verify",
            "/api/auth/reset-password",
            "/api/auth/signup",
            "/api/auth/login",
            "/api/auth/refresh-token",
            "/swagger-ui/**",
            "/api-docs/**",
            "/api/accept/**"
    };
}
