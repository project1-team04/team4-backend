package com.elice.team04backend.common.oauth.impl;

import com.elice.team04backend.common.client.NaverTokenFeignClient;
import com.elice.team04backend.common.client.NaverUserInfoFeignClient;
import com.elice.team04backend.common.config.property.OAuthProperty;
import com.elice.team04backend.common.dto.response.NaverAccessTokenResponseDto;
import com.elice.team04backend.common.dto.response.NaverAccountResponseDto;
import com.elice.team04backend.common.oauth.SocialOAuth;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class NaverOAuth implements SocialOAuth {

    private final OAuthProperty oAuthProperty;
    private final NaverTokenFeignClient naverTokenFeignClient;
    private final NaverUserInfoFeignClient naverUserInfoFeignClient;

    /*
    인가 코드를 받기 위한 리다이렉트 URL를 생성함
     */
    @Override
    public String getOauthRedirectURL() {

        SecureRandom random = new SecureRandom();
        String state = new BigInteger(130, random).toString(); // CSRF 방지 토큰

        Map<String, Object> params = new HashMap<>();
        params.put("response_type", "code");
        params.put("client_id", oAuthProperty.getNaver().getClientId());
        params.put("redirect_uri", oAuthProperty.getNaver().getRedirectUri());
        params.put("state", state);

        //parameter를 형식에 맞춰 구성해주는 함수
        String parameterString = params.entrySet().stream()
                .map(x -> x.getKey() + "=" + x.getValue())
                .collect(Collectors.joining("&"));

        String redirectURL = oAuthProperty.getNaver().getOauthUri() + "?" + parameterString;
        log.info("redirectURL = {}", redirectURL);

        return redirectURL;
    }

    /*
    인가 코드로 Naver Oauth로부터 AccessToken을 발급 받음
     */
    public NaverAccessTokenResponseDto requestAccessToken(String authCode, String state) {
        String body = "code=" + URLEncoder.encode(authCode, StandardCharsets.UTF_8)
                + "&client_id=" + oAuthProperty.getNaver().getClientId()
                + "&client_secret=" + oAuthProperty.getNaver().getClientSecret()
                + "&state=" + URLEncoder.encode(state, StandardCharsets.UTF_8)
                + "&grant_type=" + oAuthProperty.getNaver().getGrantType();

        return naverTokenFeignClient.requestAccessToken(
                MediaType.APPLICATION_FORM_URLENCODED_VALUE,
                body
        );
    }

    /*
    AccessToken으로부터 유저 정보를 얻음
     */
    public NaverAccountResponseDto requestUserInfo(String tokenType, String accessToken) {

        return naverUserInfoFeignClient.requestNaverAccount(
                 tokenType + " " + accessToken
        );
    }
}
