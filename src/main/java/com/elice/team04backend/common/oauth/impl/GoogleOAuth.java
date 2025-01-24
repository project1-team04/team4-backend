package com.elice.team04backend.common.oauth.impl;

import com.elice.team04backend.common.client.GoogleTokenFeignClient;
import com.elice.team04backend.common.client.GoogleUserInfoFeignClient;
import com.elice.team04backend.common.config.property.OAuthProperty;
import com.elice.team04backend.common.dto.response.GoogleAccessTokenResponseDto;
import com.elice.team04backend.common.dto.response.GoogleAccountResponseDto;
import com.elice.team04backend.common.oauth.SocialOAuth;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class GoogleOAuth implements SocialOAuth {

    private final OAuthProperty oAuthProperty;
    private final GoogleTokenFeignClient googleTokenFeignClient;
    private final GoogleUserInfoFeignClient googleUserInfoFeignClient;

    /*
    인가 코드를 받기 위한 리다이렉트 URL를 생성함
     */
    @Override
    public String getOauthRedirectURL() {

        Map<String, Object> params = new HashMap<>();
        params.put("client_id", oAuthProperty.getGoogle().getClientId());
        params.put("redirect_uri", oAuthProperty.getGoogle().getRedirectUri());
        params.put("response_type", "code");
        params.put("scope", oAuthProperty.getGoogle().getAccessScope());

        //parameter를 형식에 맞춰 구성해주는 함수
        String parameterString = params.entrySet().stream()
                .map(x -> x.getKey() + "=" + x.getValue())
                .collect(Collectors.joining("&"));

        String redirectURL = oAuthProperty.getGoogle().getOauthUri() + "?" + parameterString;
        log.info("redirectURL = {}", redirectURL);

        return redirectURL;
    }

    /*
    인가 코드로 Google Oauth로부터 AccessToken을 발급 받음
     */
    public GoogleAccessTokenResponseDto requestAccessToken(String authCode) {
        String body = "code=" + URLEncoder.encode(authCode, StandardCharsets.UTF_8)
                + "&client_id=" + oAuthProperty.getGoogle().getClientId()
                + "&client_secret=" + oAuthProperty.getGoogle().getClientSecret()
                + "&grant_type=" + oAuthProperty.getGoogle().getGrantType()
                + "&redirect_uri=" + oAuthProperty.getGoogle().getRedirectUri();

        return googleTokenFeignClient.requestAccessToken(
                MediaType.APPLICATION_FORM_URLENCODED_VALUE,
                body
        );
    }

    /*
    AccessToken으로부터 유저 정보를 얻음
     */
    public GoogleAccountResponseDto requestUserInfo(String tokenType, String accessToken) {

        return googleUserInfoFeignClient.requestGoogleAccount(
                 tokenType + " " + accessToken
        );
    }
}
