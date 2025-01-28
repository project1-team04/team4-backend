package com.elice.team04backend.common.client;

import com.elice.team04backend.common.dto.response.GoogleAccountResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "googleUserInfoFeignClient", url = "https://www.googleapis.com")
public interface GoogleUserInfoFeignClient {
    @GetMapping("/userinfo/v2/me")
    GoogleAccountResponseDto requestGoogleAccount(
            @RequestHeader("Authorization") String authorizationHeader
    );
}
