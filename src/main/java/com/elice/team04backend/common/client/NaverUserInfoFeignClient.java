package com.elice.team04backend.common.client;

import com.elice.team04backend.common.dto.response.NaverAccountResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "naverUserInfoFeignClient", url = "https://openapi.naver.com")
public interface NaverUserInfoFeignClient {
    @GetMapping("/v1/nid/me")
    NaverAccountResponseDto requestNaverAccount(
            @RequestHeader("Authorization") String authorizationHeader
    );
}
