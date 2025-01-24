package com.elice.team04backend.common.client;

import com.elice.team04backend.common.dto.response.KakaoAccountResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "kakaoUserInfoFeignClient", url = "https://kapi.kakao.com")
public interface KakaoUserInfoFeignClient {
    @GetMapping("/v2/user/me")
    KakaoAccountResponseDto requestKakaoAccount(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestHeader("Content-Type") String contentTypeHeader
    );
}
