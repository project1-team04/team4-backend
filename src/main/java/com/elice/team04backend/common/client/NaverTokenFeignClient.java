package com.elice.team04backend.common.client;

import com.elice.team04backend.common.dto.response.NaverAccessTokenResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "naverTokenFeignClient", url = "https://nid.naver.com/oauth2.0")
public interface NaverTokenFeignClient {
    @PostMapping(value = "/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    NaverAccessTokenResponseDto requestAccessToken(
            @RequestHeader("Content-Type") String contentType,
            @RequestBody String body
    );
}
