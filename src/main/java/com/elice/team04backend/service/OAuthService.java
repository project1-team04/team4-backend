package com.elice.team04backend.service;

import com.elice.team04backend.common.constant.SocialLoginType;
import com.elice.team04backend.common.dto.response.AccessTokenResponseDto;
import jakarta.servlet.http.HttpServletResponse;

public interface OAuthService {
    String accessRequest(SocialLoginType socialLoginType);

    AccessTokenResponseDto oAuthLoginOrJoin(SocialLoginType socialLoginType, String authCode, String state, HttpServletResponse response);
}
