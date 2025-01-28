package com.elice.team04backend.common.oauth.impl;

import com.elice.team04backend.common.client.KakaoTokenFeignClient;
import com.elice.team04backend.common.client.KakaoUserInfoFeignClient;
import com.elice.team04backend.common.config.property.OAuthProperty;
import com.elice.team04backend.common.dto.response.KakaoAccessTokenResponseDto;
import com.elice.team04backend.common.dto.response.KakaoAccountResponseDto;
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
public class KakaoOAuth implements SocialOAuth {

    private final OAuthProperty oAuthProperty;
    private final KakaoTokenFeignClient kakaoTokenFeignClient;
    private final KakaoUserInfoFeignClient kakaoUserInfoFeignClient;

    /*
    인가 코드를 받고 해당 인가코드로 리다이렉트 URL를 생성함
     */
    @Override
    public String getOauthRedirectURL() {

        Map<String, Object> params = new HashMap<>();
        params.put("response_type", "code");
        params.put("client_id", oAuthProperty.getKakao().getClientId());
        params.put("redirect_uri", oAuthProperty.getKakao().getRedirectUri());

        //parameter를 형식에 맞춰 구성해주는 함수
        String parameterString = params.entrySet().stream()
                .map(x -> x.getKey() + "=" + x.getValue())
                .collect(Collectors.joining("&"));

        String redirectURL = oAuthProperty.getKakao().getOauthUri() + "?" + parameterString;
        log.info("redirectURL = {}", redirectURL);

        return redirectURL;
    }

    /*
    인가 코드로 Kakao Oauth로부터 AccessToken을 발급 받음
     */
    public KakaoAccessTokenResponseDto requestAccessToken(String authCode) {
        String body = "code=" + URLEncoder.encode(authCode, StandardCharsets.UTF_8)
                + "&client_id=" + oAuthProperty.getKakao().getClientId()
                + "&redirect_uri=" + oAuthProperty.getKakao().getRedirectUri()
                + "&grant_type=" + oAuthProperty.getKakao().getGrantType();

        return kakaoTokenFeignClient.requestAccessToken(
                MediaType.APPLICATION_FORM_URLENCODED_VALUE,
                body
        );
    }

    /*
    AccessToken으로부터 유저 정보를 얻음
     */
    public KakaoAccountResponseDto requestUserInfo(String accessToken) {

        return kakaoUserInfoFeignClient.requestKakaoAccount(
                "Bearer " + accessToken,
          MediaType.APPLICATION_FORM_URLENCODED_VALUE
        );
    }
}
