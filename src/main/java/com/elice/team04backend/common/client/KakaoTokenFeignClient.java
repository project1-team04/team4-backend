package com.elice.team04backend.common.client;

import com.elice.team04backend.common.dto.response.KakaoAccessTokenResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "kakaoTokenFeignClient", url = "https://kauth.kakao.com/oauth")
public interface KakaoTokenFeignClient {
    @PostMapping(value = "/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    KakaoAccessTokenResponseDto requestAccessToken(
            @RequestHeader("Content-Type") String contentType,
            @RequestBody String body
    );
}
