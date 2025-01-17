package com.elice.team04backend.common.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SignUpRequestDto {
    private String email;
    private String username;
    private String password;
    private String profileImageUrl;
}
