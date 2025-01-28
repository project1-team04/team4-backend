package com.elice.team04backend.common.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GoogleAccessTokenResponseDto {
    @JsonProperty(value = "access_token")
    private final String accessToken;

    @JsonProperty(value = "expires_in")
    private final String expiresIn;

    private final String scope;

    @JsonProperty(value = "token_type")
    private final String tokenType;

    @JsonProperty(value = "id_token")
    private final String idToken;
}
